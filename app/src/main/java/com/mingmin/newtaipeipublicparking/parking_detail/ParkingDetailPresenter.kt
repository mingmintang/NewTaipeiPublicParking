package com.mingmin.newtaipeipublicparking.parking_detail

import com.google.android.gms.maps.model.LatLng
import com.mingmin.newtaipeipublicparking.data.Route
import com.mingmin.newtaipeipublicparking.data.RouteRepository
import com.mingmin.newtaipeipublicparking.map.GoogleMapProvider
import com.mingmin.newtaipeipublicparking.util.EspressoIdlingResource
import com.mingmin.newtaipeipublicparking.util.schedulers.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver

class ParkingDetailPresenter(private val googleMapProvider: GoogleMapProvider,
                             private val disposables: CompositeDisposable,
                             private val routeRepository: RouteRepository,
                             private val schedulerProvider: SchedulerProvider,
                             private val parkingDetailView: ParkingDetailContract.View)
    : ParkingDetailContract.ActionsListener {

    override fun loadMap() {
        parkingDetailView.showMapLoading(true)
        EspressoIdlingResource.increment()
        googleMapProvider.loadMap { googleMap ->
            parkingDetailView.showMap(googleMap)
            parkingDetailView.showMapLoading(false)
            EspressoIdlingResource.decrement()
        }
    }

    override fun loadMyLocation() {
        EspressoIdlingResource.increment()
        googleMapProvider.loadMyLocation { location ->
            parkingDetailView.showMyLocation(LatLng(location.latitude, location.longitude))
            parkingDetailView.showRoutesButton()
            EspressoIdlingResource.decrement()
        }
    }

    override fun loadRoutes(myLocation: LatLng,
                            parkingLotLocation: LatLng,
                            googleDirectionsKey: String) {
        parkingDetailView.showMapLoading(true)
        disposables.add(
            routeRepository.getRoutes(myLocation, parkingLotLocation, googleDirectionsKey)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeWith(object : DisposableSingleObserver<List<Route>>() {
                    override fun onSuccess(routes: List<Route>) {
                        parkingDetailView.showRoutes(myLocation, parkingLotLocation, routes)
                        parkingDetailView.showMapLoading(false)
                    }
                    override fun onError(e: Throwable) {
                        parkingDetailView.showLoadRoutesFail()
                        parkingDetailView.showMapLoading(false)
                    }
                })
        )
    }
}