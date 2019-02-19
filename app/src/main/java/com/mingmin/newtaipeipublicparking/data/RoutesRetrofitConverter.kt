package com.mingmin.newtaipeipublicparking.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.mingmin.newtaipeipublicparking.utils.Converts
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class RoutesRetrofitConverter : Converter<ResponseBody, ArrayList<Route>> {
    override fun convert(body: ResponseBody): ArrayList<Route>? {
        val json = body.string()
        val mapper = ObjectMapper().registerKotlinModule()
        val routes = ArrayList<Route>()
        val routesNode = mapper.readTree(json)["routes"]
        return if (routesNode == null) {
            null
        } else {
            routesNode.forEach { routeNode ->
                val stepNodes = routeNode["legs"][0]["steps"]
                val encodedPoints = ArrayList<String>()
                stepNodes.forEach { stepNode -> encodedPoints.add(stepNode["polyline"]["points"].asText()) }
                val points = encodedPoints.flatMap { encodedPoint ->
                    Converts.decodePolylinePoints(encodedPoint)
                }
                routes.add(Route(points))
            }
            routes
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
                    return RoutesRetrofitConverter()
                }
            }
        }
    }
}