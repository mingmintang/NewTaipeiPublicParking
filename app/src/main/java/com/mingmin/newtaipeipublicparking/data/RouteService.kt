package com.mingmin.newtaipeipublicparking.data

import com.google.android.gms.maps.model.LatLng

interface RouteService {
    fun getRoutes(from: LatLng, to: LatLng, googleDirectionsKey: String,
                  callback: (ArrayList<Route>?) -> Unit)

    companion object {
        const val DIRECTIONS_API_URL = "https://maps.googleapis.com/maps/api/directions/json"
    }
}