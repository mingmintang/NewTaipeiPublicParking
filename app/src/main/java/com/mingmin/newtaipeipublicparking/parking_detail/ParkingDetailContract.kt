package com.mingmin.newtaipeipublicparking.parking_detail

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.mingmin.newtaipeipublicparking.data.Route

interface ParkingDetailContract {
    interface View {
        fun showMap(googleMap: GoogleMap)
        fun showMyLocation(myLocation: LatLng)
        fun showRoutes(myLocation: LatLng, parkingLotLocation: LatLng, routes: List<Route>)
        fun showRoutesButton()
        fun showMapLoading(isEnabled: Boolean)
        fun showLoadMapFail()
        fun showLoadRoutesFail()
    }

    interface ActionsListener {
        fun loadMap()
        fun loadMyLocation()
        fun loadRoutes(
            myLocation: LatLng,
            parkingLotLocation: LatLng,
            googleDirectionsKey: String
        )
    }
}