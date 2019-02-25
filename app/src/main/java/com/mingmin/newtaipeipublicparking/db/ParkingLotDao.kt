package com.mingmin.newtaipeipublicparking.db

import android.content.Context
import com.mingmin.newtaipeipublicparking.data.ParkingLot

interface ParkingLotDao {
    fun count(): Int
    fun insert(parkingLot: ParkingLot)
    fun insertAll(parkingLots: Collection<ParkingLot>)
    fun update(parkingLot: ParkingLot)
    fun updateAll(parkingLots: Collection<ParkingLot>)
    fun deleteAll()
    fun queryAll(): ArrayList<ParkingLot>?
    fun queryByAreaAndKeyword(area: String?, keyword: String?): ArrayList<ParkingLot>?

    companion object {
        fun newInstance(context: Context): ParkingLotDao {
            return ParkingLotDaoImpl(context)
        }
    }
}