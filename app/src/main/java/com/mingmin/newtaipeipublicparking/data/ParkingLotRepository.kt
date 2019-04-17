package com.mingmin.newtaipeipublicparking.data

import com.mingmin.newtaipeipublicparking.data.local.ParkingLotDao
import com.mingmin.newtaipeipublicparking.data.remote.ParkingLotService
import io.reactivex.Single

interface ParkingLotRepository {
    fun isLocalParkingLotsEmpty(): Single<Boolean>
    fun getLocalParkingLots(area: String?, keyword: String?): Single<List<ParkingLot>>
    fun getAndSaveRemoteParkingLots(): Single<List<ParkingLot>>

    companion object {
        private var repository: ParkingLotRepository? = null
        fun getInstance(parkingLotDao: ParkingLotDao,
                        parkingLotService: ParkingLotService): ParkingLotRepository {
            if (repository == null) {
                repository = ParkingLotRepositoryImpl(parkingLotDao, parkingLotService)
            }
            return repository!!
        }
    }
}