package com.mingmin.newtaipeipublicparking.utils

import com.google.android.gms.maps.model.LatLng


class Converts {
    companion object {
        /**
         * TWD97 convert to WGS84 coordinate
         * Reference: https://github.com/Chao-wei-chu/TWD97_change_to_WGS
         */
        fun twd97ToLatLong(tw97x: Double, tw97y: Double): LatLng {
            val dx = 250000.0
            val dy = 0.0
            val lon0 = 121.0 * Math.PI / 180.0
            val k0 = 0.9999
            val a = 6378137.0
            val b = 6356752.314245
            val e = Math.pow(1 - Math.pow(b, 2.0) / Math.pow(a, 2.0), 0.5)

            val x = tw97x - dx
            val y = tw97y - dy

            // Calculate the Meridional Arc
            val M = y / k0

            // Calculate Footprint Latitude
            val mu = M / (a * (1.0 - Math.pow(e, 2.0) / 4.0 - 3 * Math.pow(e, 4.0) / 64.0 - 5 * Math.pow(e, 6.0) / 256.0))
            val e1 = (1.0 - Math.pow(1.0 - Math.pow(e, 2.0), 0.5)) / (1.0 + Math.pow(1.0 - Math.pow(e, 2.0), 0.5))

            val J1 = 3 * e1 / 2 - 27 * Math.pow(e1, 3.0) / 32.0
            val J2 = 21 * Math.pow(e1, 2.0) / 16 - 55 * Math.pow(e1, 4.0) / 32.0
            val J3 = 151 * Math.pow(e1, 3.0) / 96.0
            val J4 = 1097 * Math.pow(e1, 4.0) / 512.0

            val fp = mu + J1 * Math.sin(2 * mu) + J2 * Math.sin(4 * mu) + J3 * Math.sin(6 * mu) + J4 * Math.sin(8 * mu)

            // Calculate Latitude and Longitude
            val e2 = Math.pow(e * a / b, 2.0)
            val C1 = Math.pow(e2 * Math.cos(fp), 2.0)
            val T1 = Math.pow(Math.tan(fp), 2.0)
            val R1 = a * (1 - Math.pow(e, 2.0)) / Math.pow(1 - Math.pow(e, 2.0) * Math.pow(Math.sin(fp), 2.0), 3.0 / 2.0)
            val N1 = a / Math.pow(1 - Math.pow(e, 2.0) * Math.pow(Math.sin(fp), 2.0), 0.5)
            val D = x / (N1 * k0)

            // Latitude
            val Q1 = N1 * Math.tan(fp) / R1
            val Q2 = Math.pow(D, 2.0) / 2.0
            val Q3 = (5.0 + 3 * T1 + 10 * C1 - 4 * Math.pow(C1, 2.0) - 9 * e2) * Math.pow(D, 4.0) / 24.0
            val Q4 = (61.0 + 90 * T1 + 298 * C1 + 45 * Math.pow(T1, 2.0) - 3 * Math.pow(C1, 2.0) - 252 * e2) * Math.pow(D, 6.0) / 720.0
            val lat = fp - Q1 * (Q2 - Q3 + Q4)

            // Longitude
            val Q6 = (1.0 + 2 * T1 + C1) * Math.pow(D, 3.0) / 6
            val Q7 = (5 - 2 * C1 + 28 * T1 - 3 * Math.pow(C1, 2.0) + 8 * e2 + 24 * Math.pow(T1, 2.0)) * Math.pow(D, 5.0) / 120.0
            val lon = lon0 + (D - Q6 + Q7) / Math.cos(fp)

            return LatLng(Math.toDegrees(lat), Math.toDegrees(lon))
        }
    }
}
