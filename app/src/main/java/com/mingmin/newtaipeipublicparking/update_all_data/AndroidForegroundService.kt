package com.mingmin.newtaipeipublicparking.update_all_data

import android.content.Context
import android.content.Intent

class AndroidForegroundService(val context: Context) :
    ForegroundService {
    override fun startUpdateAllData() {
        val intent = Intent(context, UpdateAllDataForegroundService::class.java)
        context.startService(intent)
    }
}