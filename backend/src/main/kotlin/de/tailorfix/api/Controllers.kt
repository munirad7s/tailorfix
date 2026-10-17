package de.tailorfix.api

import de.tailorfix.domain.GeoPoint
import de.tailorfix.domain.ReturnIntentService
import de.tailorfix.domain.TailorMatchingService
import de.tailorfix.domain.VoucherService
import de.tailorfix.persistence.OrderItemRepository
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.UUID

/** Demo data for the prototype: the "my orders" screen of one fictitious customer. */
@RestController
class OrderItemsController(private val orderItems: OrderItemRepository) {
    @GetMapping("/order-items")
    fun list(): List<OrderItemDto> = orderItems.findAll().sortedByDescending { it.deliveredAt }.map { OrderItemDto.from(it) }
}

@RestController
class TailorsController(private val matching: TailorMatchingService) {
    @GetMapping("/tailors")
    fun nearby(
        @RequestParam lat: Double,
        @RequestParam lon: Double,
        @RequestParam(name = "radius_km", required = false) radiusKm: Double?,
    ): List<TailorDto> = matching.findNearby(GeoPoint(lat, lon), radiusKm).map { TailorDto.from(it) }
}

@RestController
class ReturnIntentsController(private val service: ReturnIntentService) {
    @PostMapping("/return-intents")
    fun create(@Valid @RequestBody request: CreateReturnIntentRequest): ResponseEntity<ReturnIntentDto> {
        val location = request.location?.let { GeoPoint(it.lat!!, it.lon!!) }
        val result = service.create(request.orderItemId!!, request.reason!!, location)
        val uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(result.intent.id).toUri()
        return ResponseEntity.created(uri).body(ReturnIntentDto.from(result))
    }

    @GetMapping("/return-intents/{id}")
    fun get(@PathVariable id: UUID): ReturnIntentDto = ReturnIntentDto.from(service.get(id))
}

@RestController
class VouchersController(private val service: VoucherService) {
    @PostMapping("/vouchers")
    fun issue(@Valid @RequestBody request: CreateVoucherRequest): ResponseEntity<VoucherDto> {
        val view = service.issue(request.offerId!!, request.tailorId!!)
        val uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{code}").buildAndExpand(view.voucher.code).toUri()
        return ResponseEntity.created(uri).body(VoucherDto.from(view))
    }

    @GetMapping("/vouchers/{code}")
    fun get(@PathVariable code: String): VoucherDto = VoucherDto.from(service.get(code))

    /** The tailor scans the QR code or types the code; a redemption is a resource, not an action (guideline: no verbs in URLs). */
    @PostMapping("/vouchers/{code}/redemptions")
    fun redeem(@PathVariable code: String): ResponseEntity<RedemptionDto> {
        val view = service.redeem(code)
        val dto = RedemptionDto(
            voucherCode = view.voucher.code,
            redeemedAt = view.voucher.redeemedAt!!,
            payoutToTailor = MoneyDto.ofCents(view.voucher.valueCents),
            voucher = VoucherDto.from(view),
        )
        return ResponseEntity.status(201).body(dto)
    }
}
