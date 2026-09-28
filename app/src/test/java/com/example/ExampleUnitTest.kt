package com.example

import com.example.util.LocationHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testHaversineDistance() {
        // San Francisco to Oakland (~13.5 km)
        val sfLat = 37.7749
        val sfLng = -122.4194
        val oakLat = 37.8044
        val oakLng = -122.2711

        val dist = LocationHelper.calculateDistanceKm(sfLat, sfLng, oakLat, oakLng)
        assertTrue(dist > 10.0 && dist < 16.0)
    }

    @Test
    fun testBearingCalculation() {
        // North bearing
        val bearingNorth = LocationHelper.calculateBearing(0.0, 0.0, 1.0, 0.0)
        assertEquals(0f, bearingNorth, 1.0f)
    }

    @Test
    fun testFormatDistance() {
        val near = LocationHelper.formatDistance(0.45)
        assertEquals("450m away", near)

        val far = LocationHelper.formatDistance(4.5)
        assertEquals("4.5 km away", far)
    }
}
