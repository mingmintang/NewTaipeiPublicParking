package com.mingmin.newtaipeipublicparking.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.mingmin.newtaipeipublicparking.data.ParkingLotService.Companion.PARKING_LOTS_URL
import com.mingmin.newtaipeipublicparking.utils.Downloads

class ParkingLotServiceImpl : ParkingLotService {
    override fun getAllParkingLots(callback: (List<ParkingLot>?) -> Unit) {
        Downloads.okHttpDownload(PARKING_LOTS_URL) { json ->
            if (json == null) {
                callback(null)
            } else {
                val mapper = ObjectMapper().registerKotlinModule()
                val parkingLotNode = mapper.readTree(json)["result"]["records"]
                if (parkingLotNode == null) {
                    callback(null)
                } else {
                    val parkingLots = mapper.readValue<List<ParkingLot>>(parkingLotNode.toString())
                    callback(parkingLots)
                }
            }
        }
    }
}