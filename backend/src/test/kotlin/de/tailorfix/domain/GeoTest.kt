package de.tailorfix.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class GeoTest {
    @Test
    fun `distance between FAU WiSo Nuremberg and Erlangen Hugenottenplatz is about 17 km`() {
        val wiso = GeoPoint(49.4525, 11.0872)
        val erlangen = GeoPoint(49.5964, 11.0046)

        assertEquals(17.0, Geo.distanceKm(wiso, erlangen), 1.0)
    }

    @Test
    fun `same point has zero distance`() {
        val p = GeoPoint(49.5, 11.0)
        assertEquals(0.0, Geo.distanceKm(p, p), 0.0001)
    }
}
