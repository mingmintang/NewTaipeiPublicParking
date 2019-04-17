package com.mingmin.newtaipeipublicparking

import android.content.Context
import com.mingmin.newtaipeipublicparking.data.ParkingLotRepository
import com.mingmin.newtaipeipublicparking.data.RouteRepository
import com.mingmin.newtaipeipublicparking.data.local.ParkingLotDao
import com.mingmin.newtaipeipublicparking.data.remote.ParkingLotService
import com.mingmin.newtaipeipublicparking.data.remote.RouteService
import com.mingmin.newtaipeipublicparking.util.schedulers.AndroidSchedulerProvider
import com.mingmin.newtaipeipublicparking.util.schedulers.SchedulerProvider

object Injection {
    fun provideParkingLotRepository(context: Context): ParkingLotRepository {
        return ParkingLotRepository.getInstance(
            ParkingLotDao.newInstance(context),
            ParkingLotService.create()
        )
    }

    fun provideRouteRepository(): RouteRepository {
        return RouteRepository.getInstance(RouteService.create())
    }

    fun provideSchedulerProvider(): SchedulerProvider {
        return AndroidSchedulerProvider()
    }
}