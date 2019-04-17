package com.mingmin.newtaipeipublicparking.data.remote

import com.mingmin.newtaipeipublicparking.data.ParkingLot
import com.mingmin.newtaipeipublicparking.data.ParkingLotsRetrofitConverter
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET

interface ParkingLotService {
    /**
     * Get public parking lots of New Taipei.
     */
    @GET("382000000A-000225-002")
    fun getAllParkingLots(): Single<List<ParkingLot>>

    companion object {
        const val NTPC_API_BASE_URL = "http://data.ntpc.gov.tw/api/v1/rest/datastore/"

        fun create(): ParkingLotService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(ParkingLotsRetrofitConverter.buildFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(NTPC_API_BASE_URL)
                .build()
            return retrofit.create(ParkingLotService::class.java)
        }
    }
}