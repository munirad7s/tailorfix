package de.tailorfix.domain

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object Geo {
    private const val EARTH_RADIUS_KM = 6371.0088

    /** Great-circle distance in kilometres (Haversine). Good enough for "tailors within 5 km". */
    fun distanceKm(from: GeoPoint, to: GeoPoint): Double {
        val dLat = Math.toRadians(to.lat - from.lat)
        val dLon = Math.toRadians(to.lon - from.lon)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(from.lat)) * cos(Math.toRadians(to.lat)) * sin(dLon / 2) * sin(dLon / 2)
        return 2 * EARTH_RADIUS_KM * atan2(sqrt(a), sqrt(1 - a))
    }
}
