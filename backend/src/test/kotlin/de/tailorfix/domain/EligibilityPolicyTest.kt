package de.tailorfix.domain

import de.tailorfix.config.EconomicsProperties
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EligibilityPolicyTest {

    private val policy = EligibilityPolicy(EconomicsProperties(returnCostCents = 1100, minItemPriceCents = 5000))

    @Test
    fun `trousers that are too long get a hem voucher`() {
        val decision = policy.evaluate(Category.TROUSERS, 8995, ReturnReason.TOO_LONG)

        assertTrue(decision.eligible)
        assertEquals(AlterationType.HEM_SHORTEN, decision.alterationType)
        assertEquals(1500, decision.voucherValueCents)
        assertFalse(decision.requiresTailorCheck)
        // saving = return cost 11.00 + retained margin 40 % of 89.95 (35.98) - voucher 15.00
        assertEquals(1100 + 3598 - 1500, decision.estimatedSavingCents)
    }

    @Test
    fun `voucher is capped at 35 percent of the item price`() {
        val decision = policy.evaluate(Category.BLAZERS, 5000, ReturnReason.SLEEVES_TOO_LONG)

        assertTrue(decision.eligible)
        assertEquals(1750, decision.voucherValueCents)
    }

    @Test
    fun `shoes are never eligible`() {
        val decision = policy.evaluate(Category.SHOES, 12999, ReturnReason.TOO_WIDE)

        assertFalse(decision.eligible)
        assertEquals("category_not_alterable", decision.rejectionReason)
    }

    @Test
    fun `cheap items fall back to a normal return`() {
        val decision = policy.evaluate(Category.SHIRTS, 1999, ReturnReason.TOO_LONG)

        assertFalse(decision.eligible)
        assertEquals("item_price_below_minimum", decision.rejectionReason)
    }

    @Test
    fun `taste and defects are not a tailoring problem`() {
        assertEquals("reason_not_fixable_by_tailor", policy.evaluate(Category.DRESSES, 7999, ReturnReason.DO_NOT_LIKE).rejectionReason)
        assertEquals("reason_not_fixable_by_tailor", policy.evaluate(Category.DRESSES, 7999, ReturnReason.DEFECTIVE).rejectionReason)
        assertEquals("reason_not_fixable_by_tailor", policy.evaluate(Category.TROUSERS, 8995, ReturnReason.TOO_SHORT).rejectionReason)
    }

    @Test
    fun `letting out seams needs a tailor check`() {
        val decision = policy.evaluate(Category.DRESSES, 7999, ReturnReason.TOO_TIGHT)

        assertTrue(decision.eligible)
        assertTrue(decision.requiresTailorCheck)
    }
}
