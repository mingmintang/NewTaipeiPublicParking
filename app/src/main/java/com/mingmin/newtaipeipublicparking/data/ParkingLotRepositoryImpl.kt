package com.mingmin.newtaipeipublicparking.data

import com.mingmin.newtaipeipublicparking.data.local.ParkingLotDao
import com.mingmin.newtaipeipublicparking.data.remote.ParkingLotService
import io.reactivex.Single

class ParkingLotRepositoryImpl(private val parkingLotDao: ParkingLotDao,
                               private val parkingLotService: ParkingLotService)
    : ParkingLotRepository {
    override fun isLocalParkingLotsEmpty(): Single<Boolean> {
        return Single.fromCallable { parkingLotDao.count() }
            .flatMap { count -> Single.just(count <= 0) }
    }

    override fun getLocalParkingLots(area: String?, keyword: String?): Single<List<ParkingLot>> {
        return Single.fromCallable { parkingLotDao.queryByAreaAndKeyword(area, keyword) }
    }

    override fun getAndSaveRemoteParkingLots(): Single<List<ParkingLot>> {
        return parkingLotService.getAllParkingLots()
            .map { parkingLots -> parkingLotDao.updateAll(parkingLots); parkingLots }
    }
}