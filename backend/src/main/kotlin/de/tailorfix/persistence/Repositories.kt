package de.tailorfix.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TailorRepository : JpaRepository<TailorEntity, UUID> {
    fun findAllByCertifiedTrue(): List<TailorEntity>
}

interface OrderItemRepository : JpaRepository<OrderItemEntity, UUID>

interface ReturnIntentRepository : JpaRepository<ReturnIntentEntity, UUID>

interface TailorFixOfferRepository : JpaRepository<TailorFixOfferEntity, UUID> {
    fun findByReturnIntentId(returnIntentId: UUID): TailorFixOfferEntity?
}

interface VoucherRepository : JpaRepository<VoucherEntity, UUID> {
    fun findByCode(code: String): VoucherEntity?
    fun findByOfferId(offerId: UUID): VoucherEntity?
}
