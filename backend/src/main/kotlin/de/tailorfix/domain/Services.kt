package de.tailorfix.domain

import de.tailorfix.config.EconomicsProperties
import de.tailorfix.config.MatchingProperties
import de.tailorfix.persistence.OrderItemEntity
import de.tailorfix.persistence.OrderItemRepository
import de.tailorfix.persistence.ReturnIntentEntity
import de.tailorfix.persistence.ReturnIntentRepository
import de.tailorfix.persistence.TailorEntity
import de.tailorfix.persistence.TailorFixOfferEntity
import de.tailorfix.persistence.TailorFixOfferRepository
import de.tailorfix.persistence.TailorRepository
import de.tailorfix.persistence.VoucherEntity
import de.tailorfix.persistence.VoucherRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.UUID

class NotFoundException(val resource: String, val id: String) : RuntimeException("$resource $id not found")
class ConflictException(message: String) : RuntimeException(message)
class UnprocessableException(message: String) : RuntimeException(message)

data class NearbyTailor(val tailor: TailorEntity, val distanceKm: Double)

@Service
class TailorMatchingService(
    private val tailors: TailorRepository,
    private val matching: MatchingProperties,
) {
    fun findNearby(location: GeoPoint, radiusKm: Double? = null, alteration: AlterationType? = null): List<NearbyTailor> {
        val radius = radiusKm ?: matching.defaultRadiusKm
        return tailors.findAllByCertifiedTrue()
            .asSequence()
            .filter { alteration == null || alteration.name in it.services.split(',') }
            .map { NearbyTailor(it, Geo.distanceKm(location, GeoPoint(it.lat, it.lon))) }
            .filter { it.distanceKm <= radius }
            .sortedBy { it.distanceKm }
            .take(matching.maxResults)
            .toList()
    }
}

data class ReturnIntentResult(
    val intent: ReturnIntentEntity,
    val item: OrderItemEntity,
    val offer: TailorFixOfferEntity?,
    val nearbyTailors: List<NearbyTailor>,
)

@Service
class ReturnIntentService(
    private val orderItems: OrderItemRepository,
    private val returnIntents: ReturnIntentRepository,
    private val offers: TailorFixOfferRepository,
    private val policy: EligibilityPolicy,
    private val matchingService: TailorMatchingService,
    private val economics: EconomicsProperties,
    private val clock: Clock,
) {
    @Transactional
    fun create(orderItemId: UUID, reason: ReturnReason, location: GeoPoint?): ReturnIntentResult {
        val item = orderItems.findById(orderItemId).orElseThrow { NotFoundException("order_item", orderItemId.toString()) }
        val decision = policy.evaluate(item.category, item.priceCents, reason)
        val now = clock.instant()

        val intent = returnIntents.save(
            ReturnIntentEntity(
                id = UUID.randomUUID(),
                orderItemId = item.id,
                reason = reason,
                eligible = decision.eligible,
                rejectionReason = decision.rejectionReason,
                customerLat = location?.lat,
                customerLon = location?.lon,
                createdAt = now,
            ),
        )

        if (!decision.eligible) {
            return ReturnIntentResult(intent, item, null, emptyList())
        }

        val offer = offers.save(
            TailorFixOfferEntity(
                id = UUID.randomUUID(),
                returnIntentId = intent.id,
                alterationType = decision.alterationType!!,
                voucherValueCents = decision.voucherValueCents,
                estimatedReturnCostCents = decision.estimatedReturnCostCents,
                estimatedSavingCents = decision.estimatedSavingCents,
                estimatedCo2SavedGrams = decision.estimatedCo2SavedGrams,
                requiresTailorCheck = decision.requiresTailorCheck,
                expiresAt = now.plus(Duration.ofDays(economics.voucherValidityDays)),
            ),
        )
        val nearby = location?.let { matchingService.findNearby(it, alteration = offer.alterationType) } ?: emptyList()
        return ReturnIntentResult(intent, item, offer, nearby)
    }

    @Transactional(readOnly = true)
    fun get(id: UUID): ReturnIntentResult {
        val intent = returnIntents.findById(id).orElseThrow { NotFoundException("return_intent", id.toString()) }
        val item = orderItems.findById(intent.orderItemId).orElseThrow { NotFoundException("order_item", intent.orderItemId.toString()) }
        val offer = offers.findByReturnIntentId(intent.id)
        val lat = intent.customerLat
        val lon = intent.customerLon
        val location = if (lat != null && lon != null) GeoPoint(lat, lon) else null
        val nearby = if (offer != null && location != null) matchingService.findNearby(location, alteration = offer.alterationType) else emptyList()
        return ReturnIntentResult(intent, item, offer, nearby)
    }
}

data class VoucherView(val voucher: VoucherEntity, val tailor: TailorEntity, val offer: TailorFixOfferEntity)

@Service
class VoucherService(
    private val vouchers: VoucherRepository,
    private val offers: TailorFixOfferRepository,
    private val tailors: TailorRepository,
    private val economics: EconomicsProperties,
    private val clock: Clock,
) {
    private val random = SecureRandom()

    @Transactional
    fun issue(offerId: UUID, tailorId: UUID): VoucherView {
        val offer = offers.findById(offerId).orElseThrow { NotFoundException("tailor_fix_offer", offerId.toString()) }
        val tailor = tailors.findById(tailorId).orElseThrow { NotFoundException("tailor", tailorId.toString()) }
        if (!tailor.certified) throw UnprocessableException("tailor ${tailor.id} is not a certified partner")
        if (offer.alterationType.name !in tailor.services.split(',')) {
            throw UnprocessableException("tailor ${tailor.id} does not offer ${offer.alterationType}")
        }
        val now = clock.instant()
        if (now.isAfter(offer.expiresAt)) throw ConflictException("offer expired")
        // Fast path for the common case; the unique index on vouchers.offer_id is the real guarantee (V4 migration).
        vouchers.findByOfferId(offerId)?.let { throw ConflictException("voucher already issued for offer $offerId") }

        val voucher = vouchers.save(
            VoucherEntity(
                id = UUID.randomUUID(),
                code = generateCode(),
                offerId = offer.id,
                tailorId = tailor.id,
                valueCents = offer.voucherValueCents,
                brandShareCents = offer.voucherValueCents * economics.brandSharePercent / 100,
                status = VoucherStatus.ISSUED,
                issuedAt = now,
                expiresAt = offer.expiresAt,
                redeemedAt = null,
            ),
        )
        return VoucherView(voucher, tailor, offer)
    }

    @Transactional(readOnly = true)
    fun get(code: String): VoucherView = view(vouchers.findByCode(code) ?: throw NotFoundException("voucher", code))

    @Transactional
    fun redeem(code: String): VoucherView {
        val voucher = vouchers.findByCode(code) ?: throw NotFoundException("voucher", code)
        val now = clock.instant()
        when {
            voucher.status == VoucherStatus.REDEEMED -> throw ConflictException("voucher already redeemed")
            voucher.status == VoucherStatus.EXPIRED || now.isAfter(voucher.expiresAt) -> {
                voucher.status = VoucherStatus.EXPIRED
                throw ConflictException("voucher expired")
            }
        }
        voucher.status = VoucherStatus.REDEEMED
        voucher.redeemedAt = now
        return view(voucher)
    }

    private fun view(voucher: VoucherEntity): VoucherView {
        val tailor = tailors.findById(voucher.tailorId).orElseThrow { NotFoundException("tailor", voucher.tailorId.toString()) }
        val offer = offers.findById(voucher.offerId).orElseThrow { NotFoundException("tailor_fix_offer", voucher.offerId.toString()) }
        return VoucherView(voucher, tailor, offer)
    }

    /** 8 characters from an alphabet without 0/O and 1/I so tailors can type it from the customer's screen. */
    private fun generateCode(): String =
        (1..8).map { CODE_ALPHABET[random.nextInt(CODE_ALPHABET.length)] }.joinToString("").chunked(4).joinToString("-")

    companion object {
        private const val CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    }
}
