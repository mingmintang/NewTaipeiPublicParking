package com.mingmin.newtaipeipublicparking.data

interface ParkingLotService {
    fun getAllParkingLots(callback: (List<ParkingLot>?) -> Unit)

    companion object {
        const val PARKING_LOTS_URL = "http://data.ntpc.gov.tw/api/v1/rest/datastore/382000000A-000225-002"
    }
}