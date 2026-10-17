package de.tailorfix

import de.tailorfix.api.CreateReturnIntentRequest
import de.tailorfix.api.CreateVoucherRequest
import de.tailorfix.api.LocationDto
import de.tailorfix.api.RedemptionDto
import de.tailorfix.api.ReturnIntentDto
import de.tailorfix.api.VoucherDto
import de.tailorfix.domain.ReturnReason
import de.tailorfix.domain.VoucherStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ReturnFlowIntegrationTest(@Autowired val http: TestRestTemplate) {

    private val trousers = UUID.fromString("0f3a1c2e-8c1b-4a7f-9e1d-1a2b3c4d5e01")
    private val sneakers = UUID.fromString("0f3a1c2e-8c1b-4a7f-9e1d-1a2b3c4d5e05")
    private val nurembergCentre = LocationDto(lat = 49.4521, lon = 11.0767)

    @Test
    fun `fit problem on trousers becomes a tailor voucher that the tailor can redeem`() {
        val created = http.postForEntity<ReturnIntentDto>(
            "/return-intents",
            CreateReturnIntentRequest(orderItemId = trousers, reason = ReturnReason.TOO_LONG, location = nurembergCentre),
        )
        assertEquals(HttpStatus.CREATED, created.statusCode)
        assertNotNull(created.headers.location)
        val intent = created.body!!
        assertTrue(intent.eligible)
        val offer = assertNotNull(intent.offer)
        assertEquals(0, BigDecimal("15.00").compareTo(offer.voucherValue.amount))
        assertEquals("EUR", offer.voucherValue.currency)
        assertTrue(offer.nearbyTailors.isNotEmpty(), "seeded tailors in Nuremberg must be found within the default radius")

        val issued = http.postForEntity<VoucherDto>(
            "/vouchers",
            CreateVoucherRequest(offerId = offer.id, tailorId = offer.nearbyTailors.first().id),
        )
        assertEquals(HttpStatus.CREATED, issued.statusCode)
        val voucher = issued.body!!
        assertEquals(VoucherStatus.ISSUED, voucher.status)
        assertEquals("tailorfix:voucher:${voucher.code}", voucher.qrPayload)

        val redeemed = http.postForEntity<RedemptionDto>("/vouchers/${voucher.code}/redemptions", null)
        assertEquals(HttpStatus.CREATED, redeemed.statusCode)
        assertEquals(VoucherStatus.REDEEMED, redeemed.body!!.voucher.status)
        assertEquals(0, BigDecimal("15.00").compareTo(redeemed.body!!.payoutToTailor.amount))

        val again = http.postForEntity<String>("/vouchers/${voucher.code}/redemptions", null)
        assertEquals(HttpStatus.CONFLICT, again.statusCode)
        assertTrue(again.headers.contentType.toString().startsWith("application/problem+json"))
    }

    @Test
    fun `a voucher can only be issued at a certified tailor that offers the alteration`() {
        val intent = http.postForEntity<ReturnIntentDto>(
            "/return-intents",
            CreateReturnIntentRequest(orderItemId = trousers, reason = ReturnReason.TOO_LONG, location = nurembergCentre),
        ).body!!
        val offerId = intent.offer!!.id
        val tailorWithoutHemService = UUID.fromString("7c1e0a10-0000-4000-8000-000000000006")
        val uncertifiedTailor = UUID.fromString("7c1e0a10-0000-4000-8000-000000000021")

        val wrongService = http.postForEntity<String>("/vouchers", CreateVoucherRequest(offerId = offerId, tailorId = tailorWithoutHemService))
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, wrongService.statusCode)

        val uncertified = http.postForEntity<String>("/vouchers", CreateVoucherRequest(offerId = offerId, tailorId = uncertifiedTailor))
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, uncertified.statusCode)

        val ok = http.postForEntity<VoucherDto>("/vouchers", CreateVoucherRequest(offerId = offerId, tailorId = intent.offer!!.nearbyTailors.first().id))
        assertEquals(HttpStatus.CREATED, ok.statusCode)

        val second = http.postForEntity<String>("/vouchers", CreateVoucherRequest(offerId = offerId, tailorId = intent.offer!!.nearbyTailors.first().id))
        assertEquals(HttpStatus.CONFLICT, second.statusCode)
    }

    @Test
    fun `sneakers fall back to the normal return`() {
        val created = http.postForEntity<ReturnIntentDto>(
            "/return-intents",
            CreateReturnIntentRequest(orderItemId = sneakers, reason = ReturnReason.TOO_TIGHT, location = nurembergCentre),
        )
        assertEquals(HttpStatus.CREATED, created.statusCode)
        assertEquals(false, created.body!!.eligible)
        assertEquals("category_not_alterable", created.body!!.rejectionReason)
    }

    @Test
    fun `unknown order item yields problem json`() {
        val response = http.postForEntity<String>(
            "/return-intents",
            CreateReturnIntentRequest(orderItemId = UUID.randomUUID(), reason = ReturnReason.TOO_LONG, location = null),
        )
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertTrue(response.headers.contentType.toString().startsWith("application/problem+json"))
    }

    @Test
    fun `every response carries a flow id`() {
        val response = http.getForEntity("/order-items", String::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.headers.getFirst("X-Flow-ID"))
        assertTrue(response.body!!.contains("\"order_number\""), "JSON must be snake_case")
    }
}
