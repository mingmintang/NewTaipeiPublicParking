package com.mingmin.newtaipeipublicparking.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.android.gms.maps.model.LatLng
import com.mingmin.newtaipeipublicparking.data.RouteService.Companion.DIRECTIONS_API_URL
import com.mingmin.newtaipeipublicparking.utils.Converts
import com.mingmin.newtaipeipublicparking.utils.Downloads

class RouteServiceImpl : RouteService {
    override fun getRoutes(from: LatLng, to: LatLng, googleDirectionsKey: String,
        callback: (ArrayList<Route>?) -> Unit) {
        // add parameters for directions api url
        val origin = "origin=${from.latitude},${from.longitude}"
        val destination = "destination=${to.latitude},${to.longitude}"
        val key = "key=$googleDirectionsKey"
        val multiRoutes = "alternatives=true"
        val url = "${DIRECTIONS_API_URL}?$origin&$destination&$multiRoutes&$key"

        Downloads.okHttpDownload(url) { json ->
            if (json == null) {
                callback(null)
            } else {
                val mapper = ObjectMapper().registerKotlinModule()
                val routes = ArrayList<Route>()
                val routesNode = mapper.readTree(json)["routes"]
                if (routesNode == null) {
                    callback(null)
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
                    callback(routes)
                }
            }
        }
    }
}