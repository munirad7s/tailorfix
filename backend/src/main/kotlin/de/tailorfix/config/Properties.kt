package de.tailorfix.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "tailorfix.economics")
data class EconomicsProperties(
    val returnCostCents: Long = 1100,
    val co2PerReturnGrams: Long = 500,
    val brandSharePercent: Int = 50,
    val minItemPriceCents: Long = 5000,
    val voucherValidityDays: Long = 14,
)

@ConfigurationProperties(prefix = "tailorfix.matching")
data class MatchingProperties(
    val defaultRadiusKm: Double = 5.0,
    val maxResults: Int = 5,
)
