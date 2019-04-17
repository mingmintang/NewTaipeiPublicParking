package com.mingmin.newtaipeipublicparking.data.local

import android.content.Context
import com.mingmin.newtaipeipublicparking.data.ParkingLot

interface ParkingLotDao {
    fun count(): Int
    fun insert(parkingLot: ParkingLot)
    fun insertAll(parkingLots: Collection<ParkingLot>)
    fun update(parkingLot: ParkingLot)
    fun updateAll(parkingLots: Collection<ParkingLot>)
    fun deleteAll()
    fun queryAll(): List<ParkingLot>
    fun queryByAreaAndKeyword(area: String?, keyword: String?): List<ParkingLot>

    companion object {
        fun newInstance(context: Context): ParkingLotDao {
            return ParkingLotDaoImpl(context)
        }
    }
}