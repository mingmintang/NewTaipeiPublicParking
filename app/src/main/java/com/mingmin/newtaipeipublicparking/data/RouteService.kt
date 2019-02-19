package com.mingmin.newtaipeipublicparking.data

import com.google.android.gms.maps.model.LatLng
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RouteService {
    /**
     * Get routes from directions API.
     * alternatives=true: Get multi routes
     */
    @GET("directions/json?alternatives=true")
    fun getRoutes(
        @Query("origin") from: MapsApiLatLng,
        @Query("destination") to: MapsApiLatLng,
        @Query("key") googleDirectionsKey: String
    ): Single<ArrayList<Route>>

    companion object {
        const val MAPS_API_BASE_URL = "https://maps.googleapis.com/maps/api/"

        fun create(): RouteService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(RoutesRetrofitConverter.buildFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(MAPS_API_BASE_URL)
                .build()
            return retrofit.create(RouteService::class.java)
        }
    }
}

class MapsApiLatLng(val location: LatLng) {
    override fun toString(): String {
        return "${location.latitude},${location.longitude}"
    }
}