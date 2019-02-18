package com.mingmin.newtaipeipublicparking.parking_detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.mingmin.newtaipeipublicparking.data.RouteService

class ParkingDetailPresenter(private val routeService: RouteService,
                             private val parkingDetailView: ParkingDetailContract.View)
    : ParkingDetailContract.ActionsListener {

    override fun loadMap(mapFragment: SupportMapFragment) {
        parkingDetailView.showMapLoading(true)
        mapFragment.getMapAsync { googleMap ->
            parkingDetailView.showMap(googleMap)
            parkingDetailView.showMapLoading(false)
        }
    }

    @SuppressLint("MissingPermission")
    override fun loadMyLocation(context: Context) {
        LocationServices.getFusedLocationProviderClient(context)
            .lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val location = task.result
                location?.let {
                    parkingDetailView.showMyLocation(LatLng(it.latitude, it.longitude))
                    parkingDetailView.showRoutesButton()
                }
            }
        }
    }

    override fun loadRoutes(activity: Activity,
                            myLocation: LatLng,
                            parkingLotLocation: LatLng,
                            googleDirectionsKey: String) {
        parkingDetailView.showMapLoading(true)
        routeService.getRoutes(myLocation, parkingLotLocation, googleDirectionsKey) { routes ->
            activity.runOnUiThread {
                parkingDetailView.showMapLoading(false)
                if (routes == null) {
                    parkingDetailView.showLoadRoutesFail()
                } else {
                    parkingDetailView.showRoutes(myLocation, parkingLotLocation, routes)
                }
            }
        }
    }
}