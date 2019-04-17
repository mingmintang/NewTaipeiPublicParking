package com.mingmin.newtaipeipublicparking.data

import com.google.android.gms.maps.model.LatLng
import com.mingmin.newtaipeipublicparking.data.remote.MapsApiLatLng
import com.mingmin.newtaipeipublicparking.data.remote.RouteService
import io.reactivex.Single

class RouteRepositoryImpl(private val routeService: RouteService) : RouteRepository {
    override fun getRoutes(from: LatLng, to: LatLng, googleDirectionsKey: String): Single<List<Route>> {
        return routeService.getRoutes(MapsApiLatLng(from), MapsApiLatLng(to), googleDirectionsKey)
    }
}