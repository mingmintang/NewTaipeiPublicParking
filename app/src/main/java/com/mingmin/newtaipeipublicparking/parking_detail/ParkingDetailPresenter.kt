package com.mingmin.newtaipeipublicparking.parking_detail

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.mingmin.newtaipeipublicparking.data.MapsApiLatLng
import com.mingmin.newtaipeipublicparking.data.Route
import com.mingmin.newtaipeipublicparking.data.RouteService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class ParkingDetailPresenter(private val disposables: CompositeDisposable,
                             private val parkingDetailView: ParkingDetailContract.View)
    : ParkingDetailContract.ActionsListener {

    private val routeService = RouteService.create()

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

    override fun loadRoutes(myLocation: LatLng,
                            parkingLotLocation: LatLng,
                            googleDirectionsKey: String) {
        parkingDetailView.showMapLoading(true)
        disposables.add(
            routeService.getRoutes(MapsApiLatLng(myLocation), MapsApiLatLng(parkingLotLocation), googleDirectionsKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ArrayList<Route>>() {
                    override fun onSuccess(routes: ArrayList<Route>) {
                        parkingDetailView.showMapLoading(false)
                        parkingDetailView.showRoutes(myLocation, parkingLotLocation, routes)
                    }
                    override fun onError(e: Throwable) {
                        parkingDetailView.showMapLoading(false)
                        parkingDetailView.showLoadRoutesFail()
                    }
                })
        )
    }
}