package de.tailorfix.domain

import de.tailorfix.config.EconomicsProperties
import org.springframework.stereotype.Component

/**
 * Decides whether a return can be turned into a local tailor fix and what the voucher is worth.
 *
 * The policy is deliberately simple and fully parameterised so that the numbers can be replaced
 * after the customer and tailor interviews (course sessions 3 to 5). Everything here is pure Kotlin
 * without framework dependencies, which keeps it unit-testable.
 */
@Component
class EligibilityPolicy(private val economics: EconomicsProperties) {

    data class Decision(
        val eligible: Boolean,
        val rejectionReason: String? = null,
        val alterationType: AlterationType? = null,
        val voucherValueCents: Long = 0,
        val estimatedReturnCostCents: Long = 0,
        val estimatedSavingCents: Long = 0,
        val estimatedCo2SavedGrams: Long = 0,
        val requiresTailorCheck: Boolean = false,
    )

    fun evaluate(category: Category, priceCents: Long, reason: ReturnReason): Decision {
        if (category !in ALTERABLE_CATEGORIES) {
            return reject("category_not_alterable")
        }
        if (priceCents < economics.minItemPriceCents) {
            return reject("item_price_below_minimum")
        }
        val alteration = alterationFor(reason, category) ?: return reject("reason_not_fixable_by_tailor")

        val listPrice = alterationPriceCents(alteration, category)
        val cap = priceCents * MAX_VOUCHER_SHARE_PERCENT / 100
        val voucherValue = minOf(listPrice, cap)

        val saving = economics.returnCostCents + priceCents * RETAINED_MARGIN_PERCENT / 100
        if (voucherValue >= saving) {
            return reject("voucher_would_exceed_saving")
        }

        return Decision(
            eligible = true,
            alterationType = alteration,
            voucherValueCents = voucherValue,
            estimatedReturnCostCents = economics.returnCostCents,
            estimatedSavingCents = saving - voucherValue,
            estimatedCo2SavedGrams = economics.co2PerReturnGrams,
            requiresTailorCheck = alteration in TAILOR_CHECK_REQUIRED,
        )
    }

    private fun reject(why: String) = Decision(eligible = false, rejectionReason = why)

    private fun alterationFor(reason: ReturnReason, category: Category): AlterationType? = when (reason) {
        ReturnReason.TOO_LONG -> if (category in HEMMABLE) AlterationType.HEM_SHORTEN else null
        ReturnReason.TOO_WIDE -> AlterationType.WAIST_TAKE_IN
        ReturnReason.TOO_TIGHT -> if (category in LET_OUT_POSSIBLE) AlterationType.SEAM_LET_OUT else null
        ReturnReason.SLEEVES_TOO_LONG -> if (category in SLEEVED) AlterationType.SLEEVE_SHORTEN else null
        ReturnReason.DOES_NOT_FIT_OTHER -> AlterationType.GENERAL_FITTING
        // Fabric cannot be added, taste cannot be tailored, defects and wrong items are classic returns.
        ReturnReason.TOO_SHORT,
        ReturnReason.DO_NOT_LIKE,
        ReturnReason.DEFECTIVE,
        ReturnReason.WRONG_ITEM,
        ReturnReason.ORDERED_SEVERAL_SIZES -> null
    }

    /** Typical alteration list prices in Erlangen/Nuremberg (to be validated with the tailor interviews). */
    private fun alterationPriceCents(alteration: AlterationType, category: Category): Long = when (alteration) {
        AlterationType.HEM_SHORTEN -> if (category == Category.COATS) 2500 else 1500
        AlterationType.WAIST_TAKE_IN -> when (category) {
            Category.TROUSERS, Category.SKIRTS -> 2000
            Category.SHIRTS -> 2500
            else -> 3500
        }
        AlterationType.SEAM_LET_OUT -> 2500
        AlterationType.SLEEVE_SHORTEN -> if (category == Category.SHIRTS) 2500 else 4500
        AlterationType.GENERAL_FITTING -> 2500
    }

    companion object {
        /** Voucher never exceeds this share of the item price (business-case guard rail, see docs/02-business-case.md). */
        const val MAX_VOUCHER_SHARE_PERCENT = 35L

        /** Gross margin retained when the sale is kept instead of returned (working assumption). */
        const val RETAINED_MARGIN_PERCENT = 40L

        val ALTERABLE_CATEGORIES = setOf(
            Category.TROUSERS, Category.SUITS, Category.BLAZERS, Category.DRESSES,
            Category.SHIRTS, Category.COATS, Category.SKIRTS,
        )
        private val HEMMABLE = setOf(Category.TROUSERS, Category.DRESSES, Category.SKIRTS, Category.COATS, Category.SUITS)
        private val LET_OUT_POSSIBLE = setOf(Category.TROUSERS, Category.SUITS, Category.BLAZERS, Category.DRESSES, Category.SKIRTS)
        private val SLEEVED = setOf(Category.SHIRTS, Category.BLAZERS, Category.SUITS, Category.COATS, Category.DRESSES)
        private val TAILOR_CHECK_REQUIRED = setOf(AlterationType.SEAM_LET_OUT, AlterationType.GENERAL_FITTING)
    }
}
