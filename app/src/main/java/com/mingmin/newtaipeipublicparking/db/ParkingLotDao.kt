package com.mingmin.newtaipeipublicparking.db

import com.mingmin.newtaipeipublicparking.data.ParkingLot

interface ParkingLotDao {
    fun count(): Int
    fun close()
    fun insert(parkinglot: ParkingLot)
    fun insertAll(parkingLots: Collection<ParkingLot>)
    fun update(parkinglot: ParkingLot)
    fun updateAll(parkingLots: Collection<ParkingLot>)
    fun deleteAll()
    fun queryAll(): ArrayList<ParkingLot>?
    fun queryByAreaAndKeyword(area: String?, keyword: String?): ArrayList<ParkingLot>?
}