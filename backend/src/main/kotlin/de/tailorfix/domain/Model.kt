package de.tailorfix.domain

enum class Category {
    TROUSERS, SUITS, BLAZERS, DRESSES, SHIRTS, COATS, SKIRTS, KNITWEAR, SHOES, ACCESSORIES, SPORTSWEAR
}

/** Return reasons as a Zalando customer picks them in the return flow. */
enum class ReturnReason {
    TOO_LONG,
    TOO_SHORT,
    TOO_WIDE,
    TOO_TIGHT,
    SLEEVES_TOO_LONG,
    DOES_NOT_FIT_OTHER,
    DO_NOT_LIKE,
    DEFECTIVE,
    WRONG_ITEM,
    ORDERED_SEVERAL_SIZES,
}

enum class AlterationType {
    HEM_SHORTEN,
    WAIST_TAKE_IN,
    SEAM_LET_OUT,
    SLEEVE_SHORTEN,
    GENERAL_FITTING,
}

enum class VoucherStatus { ISSUED, REDEEMED, EXPIRED }

data class GeoPoint(val lat: Double, val lon: Double)
