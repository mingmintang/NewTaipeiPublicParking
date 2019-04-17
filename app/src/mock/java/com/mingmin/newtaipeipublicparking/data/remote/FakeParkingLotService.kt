package com.mingmin.newtaipeipublicparking.data.remote

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.mingmin.newtaipeipublicparking.data.ParkingLot
import io.reactivex.Single

class FakeParkingLotService(val context: Context): ParkingLotService {
    override fun getAllParkingLots(): Single<List<ParkingLot>> {
        return Single.fromCallable { parseJson(FAKE_FLIE) }
    }

    private fun parseJson(fileName: String): List<ParkingLot>? {
        val inputStream = context.assets.open(fileName)
        val mapper = ObjectMapper().registerKotlinModule()
        return mapper.readValue<List<ParkingLot>>(inputStream)
    }

    companion object {
        private const val FAKE_FLIE = "FakeParkingLots.json"
        fun create(context: Context): FakeParkingLotService {
            return FakeParkingLotService(context)
        }
    }
}