package com.mingmin.newtaipeipublicparking.data

interface ParkingLotRepository {

    interface LoadListener {
        fun onParkingLotsLoadSuccess(parkingLots: List<ParkingLot>)
        fun onParkingLotsLoadFail()
    }

    fun getParkingLots(
        forceUpdate: Boolean,
        area: String?,
        keyword: String?
    )

    fun setLoadListener(listener: LoadListener)
}