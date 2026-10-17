package de.tailorfix.persistence

import de.tailorfix.domain.AlterationType
import de.tailorfix.domain.Category
import de.tailorfix.domain.ReturnReason
import de.tailorfix.domain.VoucherStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "tailors")
class TailorEntity(
    @Id val id: UUID,
    val name: String,
    val street: String,
    @Column(name = "postal_code") val postalCode: String,
    val city: String,
    val lat: Double,
    val lon: Double,
    /** Comma separated alteration types the shop offers, e.g. "HEM_SHORTEN,WAIST_TAKE_IN". */
    val services: String,
    val certified: Boolean,
    @Column(name = "turnaround_days") val turnaroundDays: Int,
    @Column(name = "opening_hours") val openingHours: String?,
    val website: String?,
)

@Entity
@Table(name = "order_items")
class OrderItemEntity(
    @Id val id: UUID,
    @Column(name = "order_number") val orderNumber: String,
    val brand: String,
    val title: String,
    @Enumerated(EnumType.STRING) val category: Category,
    val size: String,
    @Column(name = "price_cents") val priceCents: Long,
    @Column(name = "image_url") val imageUrl: String?,
    @Column(name = "delivered_at") val deliveredAt: Instant,
)

@Entity
@Table(name = "return_intents")
class ReturnIntentEntity(
    @Id val id: UUID,
    @Column(name = "order_item_id") val orderItemId: UUID,
    @Enumerated(EnumType.STRING) val reason: ReturnReason,
    val eligible: Boolean,
    @Column(name = "rejection_reason") val rejectionReason: String?,
    @Column(name = "customer_lat") val customerLat: Double?,
    @Column(name = "customer_lon") val customerLon: Double?,
    @Column(name = "created_at") val createdAt: Instant,
)

@Entity
@Table(name = "tailor_fix_offers")
class TailorFixOfferEntity(
    @Id val id: UUID,
    @Column(name = "return_intent_id") val returnIntentId: UUID,
    @Enumerated(EnumType.STRING) @Column(name = "alteration_type") val alterationType: AlterationType,
    @Column(name = "voucher_value_cents") val voucherValueCents: Long,
    @Column(name = "estimated_return_cost_cents") val estimatedReturnCostCents: Long,
    @Column(name = "estimated_saving_cents") val estimatedSavingCents: Long,
    @Column(name = "estimated_co2_saved_grams") val estimatedCo2SavedGrams: Long,
    @Column(name = "requires_tailor_check") val requiresTailorCheck: Boolean,
    @Column(name = "expires_at") val expiresAt: Instant,
)

@Entity
@Table(name = "vouchers")
class VoucherEntity(
    @Id val id: UUID,
    val code: String,
    @Column(name = "offer_id") val offerId: UUID,
    @Column(name = "tailor_id") val tailorId: UUID,
    @Column(name = "value_cents") val valueCents: Long,
    @Column(name = "brand_share_cents") val brandShareCents: Long,
    @Enumerated(EnumType.STRING) var status: VoucherStatus,
    @Column(name = "issued_at") val issuedAt: Instant,
    @Column(name = "expires_at") val expiresAt: Instant,
    @Column(name = "redeemed_at") var redeemedAt: Instant?,
)
