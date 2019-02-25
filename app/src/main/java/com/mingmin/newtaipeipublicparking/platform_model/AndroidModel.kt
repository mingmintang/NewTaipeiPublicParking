package com.mingmin.newtaipeipublicparking.platform_model

import android.content.Context
import android.content.Intent
import com.mingmin.newtaipeipublicparking.db.ParkingLotDao
import com.mingmin.newtaipeipublicparking.update_all_data.UpdateAllDataForegroundService

class AndroidModel(val context: Context) : PlatformModel {
    override fun getParkingLotDao(): ParkingLotDao {
        return ParkingLotDao.newInstance(context)
    }

    override fun startUpdateAllDataForegroundService() {
        val intent = Intent(context, UpdateAllDataForegroundService::class.java)
        context.startService(intent)
    }
}