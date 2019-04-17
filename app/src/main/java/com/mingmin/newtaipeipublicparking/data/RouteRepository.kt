package com.mingmin.newtaipeipublicparking.data

import com.google.android.gms.maps.model.LatLng
import com.mingmin.newtaipeipublicparking.data.remote.RouteService
import io.reactivex.Single

interface RouteRepository {
    fun getRoutes(from: LatLng, to: LatLng, googleDirectionsKey: String): Single<List<Route>>

    companion object {
        private var repository: RouteRepository? = null
        fun getInstance(routeService: RouteService): RouteRepository {
            if (repository == null) {
                repository = RouteRepositoryImpl(routeService)
            }
            return repository!!
        }
    }
}