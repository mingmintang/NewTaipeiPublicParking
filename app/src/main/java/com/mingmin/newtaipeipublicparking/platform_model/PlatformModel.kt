package com.mingmin.newtaipeipublicparking.platform_model

import com.mingmin.newtaipeipublicparking.db.ParkingLotDao

interface PlatformModel {
    fun getParkingLotDao(): ParkingLotDao

    /**
     * Running in foreground service,
     * that task would exist even if application is closed.
     */
    fun startUpdateAllDataForegroundService()
}