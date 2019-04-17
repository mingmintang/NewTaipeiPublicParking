package com.mingmin.newtaipeipublicparking.util

import com.google.android.gms.maps.model.LatLng
import org.junit.Test
import org.junit.Assert.*

class ConvertsTest {
    @Test
    fun twd97ToLatLng() {
        val twd97x = 296882.0
        val twd97y = 2767068.0
        val expect = Converts.twd97ToLatLng(twd97x, twd97y)

        val actual = LatLng(25.010925150026413, 121.4644919656591)

        assertEquals(expect, actual)
    }

    @Test
    fun polylinePointsToLatLngs() {
        val encodedPolylinePoints = "y_}tCk}l`VLuA"
        val expect = listOf(LatLng(24.56589, 120.8215), LatLng(24.56582, 120.82193))

        val actual = Converts.polylinePointsToLatLngs(encodedPolylinePoints)

        assertEquals(expect, actual)
    }
}