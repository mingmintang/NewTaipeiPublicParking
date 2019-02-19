package com.mingmin.newtaipeipublicparking.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class ParkingLotsRetrofitConverter : Converter<ResponseBody, List<ParkingLot>>  {
    override fun convert(body: ResponseBody): List<ParkingLot>? {
        val json = body.string()
        val mapper = ObjectMapper().registerKotlinModule()
        val parkingLotNode = mapper.readTree(json)["result"]["records"]
        return if (parkingLotNode == null) {
            null
        } else {
            mapper.readValue<List<ParkingLot>>(parkingLotNode.toString())
        }
    }

    companion object {
        fun buildFactory(): Converter.Factory {
            return object : Converter.Factory() {
                override fun responseBodyConverter(
                    type: Type,
                    annotations: Array<Annotation>,
                    retrofit: Retrofit
                ): Converter<ResponseBody, *>? {
                    return ParkingLotsRetrofitConverter()
                }
            }
        }
    }
}