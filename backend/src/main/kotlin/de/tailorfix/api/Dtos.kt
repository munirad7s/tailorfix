package de.tailorfix.api

import de.tailorfix.domain.AlterationType
import de.tailorfix.domain.Category
import de.tailorfix.domain.NearbyTailor
import de.tailorfix.domain.ReturnIntentResult
import de.tailorfix.domain.ReturnReason
import de.tailorfix.domain.VoucherStatus
import de.tailorfix.domain.VoucherView
import de.tailorfix.persistence.OrderItemEntity
import de.tailorfix.persistence.TailorEntity
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

// JSON field names are snake_case (Zalando guideline "MUST property names must be snake_case"),
// configured globally via spring.jackson.property-naming-strategy.

data class LocationDto(
    @field:NotNull @field:Min(-90) @field:Max(90) val lat: Double?,
    @field:NotNull @field:Min(-180) @field:Max(180) val lon: Double?,
)

data class CreateReturnIntentRequest(
    @field:NotNull val orderItemId: UUID?,
    @field:NotNull val reason: ReturnReason?,
    @field:Valid val location: LocationDto?,
)

data class CreateVoucherRequest(
    @field:NotNull val offerId: UUID?,
    @field:NotNull val tailorId: UUID?,
)

/** Zalando guideline "Money" object: decimal amount plus ISO 4217 currency code. */
data class MoneyDto(val amount: BigDecimal, val currency: String = "EUR") {
    companion object {
        fun ofCents(cents: Long) = MoneyDto(BigDecimal.valueOf(cents, 2))
    }
}

data class OrderItemDto(
    val id: UUID,
    val orderNumber: String,
    val brand: String,
    val title: String,
    val category: Category,
    val size: String,
    val price: MoneyDto,
    val imageUrl: String?,
    val deliveredAt: Instant,
) {
    companion object {
        fun from(e: OrderItemEntity) = OrderItemDto(
            e.id, e.orderNumber, e.brand, e.title, e.category, e.size, MoneyDto.ofCents(e.priceCents), e.imageUrl, e.deliveredAt,
        )
    }
}

data class TailorDto(
    val id: UUID,
    val name: String,
    val street: String,
    val postalCode: String,
    val city: String,
    val lat: Double,
    val lon: Double,
    val services: List<AlterationType>,
    val certified: Boolean,
    val turnaroundDays: Int,
    val openingHours: String?,
    val website: String?,
    val distanceKm: Double? = null,
) {
    companion object {
        fun from(e: TailorEntity, distanceKm: Double? = null) = TailorDto(
            id = e.id,
            name = e.name,
            street = e.street,
            postalCode = e.postalCode,
            city = e.city,
            lat = e.lat,
            lon = e.lon,
            services = e.services.split(',').filter { it.isNotBlank() }.map { AlterationType.valueOf(it.trim()) },
            certified = e.certified,
            turnaroundDays = e.turnaroundDays,
            openingHours = e.openingHours,
            website = e.website,
            distanceKm = distanceKm?.let { Math.round(it * 10) / 10.0 },
        )

        fun from(n: NearbyTailor) = from(n.tailor, n.distanceKm)
    }
}

data class TailorFixOfferDto(
    val id: UUID,
    val alterationType: AlterationType,
    val voucherValue: MoneyDto,
    val estimatedReturnCost: MoneyDto,
    val estimatedSaving: MoneyDto,
    val estimatedCo2SavedGrams: Long,
    val requiresTailorCheck: Boolean,
    val expiresAt: Instant,
    val nearbyTailors: List<TailorDto>,
)

data class ReturnIntentDto(
    val id: UUID,
    val orderItem: OrderItemDto,
    val reason: ReturnReason,
    val eligible: Boolean,
    val rejectionReason: String?,
    val offer: TailorFixOfferDto?,
    val createdAt: Instant,
) {
    companion object {
        fun from(r: ReturnIntentResult) = ReturnIntentDto(
            id = r.intent.id,
            orderItem = OrderItemDto.from(r.item),
            reason = r.intent.reason,
            eligible = r.intent.eligible,
            rejectionReason = r.intent.rejectionReason,
            offer = r.offer?.let { o ->
                TailorFixOfferDto(
                    id = o.id,
                    alterationType = o.alterationType,
                    voucherValue = MoneyDto.ofCents(o.voucherValueCents),
                    estimatedReturnCost = MoneyDto.ofCents(o.estimatedReturnCostCents),
                    estimatedSaving = MoneyDto.ofCents(o.estimatedSavingCents),
                    estimatedCo2SavedGrams = o.estimatedCo2SavedGrams,
                    requiresTailorCheck = o.requiresTailorCheck,
                    expiresAt = o.expiresAt,
                    nearbyTailors = r.nearbyTailors.map { TailorDto.from(it) },
                )
            },
            createdAt = r.intent.createdAt,
        )
    }
}

data class VoucherDto(
    val code: String,
    val status: VoucherStatus,
    val value: MoneyDto,
    val brandShare: MoneyDto,
    val alterationType: AlterationType,
    val requiresTailorCheck: Boolean,
    val tailor: TailorDto,
    val qrPayload: String,
    val issuedAt: Instant,
    val expiresAt: Instant,
    val redeemedAt: Instant?,
) {
    companion object {
        fun from(v: VoucherView) = VoucherDto(
            code = v.voucher.code,
            status = v.voucher.status,
            value = MoneyDto.ofCents(v.voucher.valueCents),
            brandShare = MoneyDto.ofCents(v.voucher.brandShareCents),
            alterationType = v.offer.alterationType,
            requiresTailorCheck = v.offer.requiresTailorCheck,
            tailor = TailorDto.from(v.tailor),
            qrPayload = "tailorfix:voucher:${v.voucher.code}",
            issuedAt = v.voucher.issuedAt,
            expiresAt = v.voucher.expiresAt,
            redeemedAt = v.voucher.redeemedAt,
        )
    }
}

data class RedemptionDto(
    val voucherCode: String,
    val redeemedAt: Instant,
    val payoutToTailor: MoneyDto,
    val voucher: VoucherDto,
)
