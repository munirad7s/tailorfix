package de.tailorfix

import de.tailorfix.config.EconomicsProperties
import de.tailorfix.config.MatchingProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(EconomicsProperties::class, MatchingProperties::class)
class TailorFixApplication

fun main(args: Array<String>) {
    runApplication<TailorFixApplication>(*args)
}
