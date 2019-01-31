package com.mingmin.newtaipeipublicparking.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.android.gms.common.api.GoogleApi
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.mingmin.newtaipeipublicparking.db.ParkingDAO
import com.mingmin.newtaipeipublicparking.utils.Converts
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableMaybeObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class Downloads {
    companion object {
        val DOWNLOAD_URL = "http://data.ntpc.gov.tw/api/v1/rest/datastore/382000000A-000225-002"
        fun updateAndReadAllRecordsFromDb(parkingDAO: ParkingDAO, disposables: CompositeDisposable): Task<ArrayList<Record>> {
            val source = TaskCompletionSource<ArrayList<Record>>()

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(DOWNLOAD_URL)
                .build()
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    source.setException(e)
                }
                override fun onResponse(call: Call, response: Response) {
                    val json = response.body()?.string()
                    val mapper = ObjectMapper().registerKotlinModule()
                    if (json == null) {
                        source.setException(Exception("json data of parking is null"))
                    } else {
                        disposables.add(Maybe.fromCallable {
                            val downloadObject = mapper.readValue<DownloadObject>(json)
                            parkingDAO.updateAll(downloadObject)
                            parkingDAO.queryAll()
                        }.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(object : DisposableMaybeObserver<ArrayList<Record>>() {
                                override fun onSuccess(records: ArrayList<Record>) {
                                    source.setResult(records)
                                }
                                override fun onComplete() {
                                    source.setException(Exception("query records is empty"))
                                }
                                override fun onError(e: Throwable) {
                                    source.setException(Exception(e))
                                }
                            })
                        )
                    }
                }
            })

            return source.task
        }

        val DIRECTIONS_API_URL = "https://maps.googleapis.com/maps/api/directions/json"
        fun getRoutesFromDirectionsAPI(from: LatLng, to: LatLng, apiKey: String): Task<ArrayList<List<LatLng>>> {
            val source = TaskCompletionSource<ArrayList<List<LatLng>>>()

            // add parameters for directions api url
            val origin = "origin=${from.latitude},${from.longitude}"
            val destination = "destination=${to.latitude},${to.longitude}"
            val key = "key=$apiKey"
            val multiRoutes = "alternatives=true"
            val url = "$DIRECTIONS_API_URL?$origin&$destination&$multiRoutes&$key"

            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    source.setException(e)
                }
                override fun onResponse(call: Call, response: Response) {
                    val json = response.body()?.string()
                    val mapper = ObjectMapper().registerKotlinModule()
                    if (json == null) {
                        source.setException(Exception("json data of directions is null"))
                    } else {
                        val routes = ArrayList<List<LatLng>>()
                        val rootNode = mapper.readTree(json)
                        val routeNodes = rootNode["routes"]
                        routeNodes.forEach { routeNode ->
                            val stepNodes = routeNode["legs"][0]["steps"]
                            val encodedPoints = ArrayList<String>()
                            stepNodes.forEach { stepNode -> encodedPoints.add(stepNode["polyline"]["points"].asText()) }
                            val points = encodedPoints.flatMap { encodedPoint ->
                                Converts.decodePolylinePoints(encodedPoint)
                            }
                            routes.add(points)
                        }
                        source.setResult(routes)
                    }
                }
            })
            return source.task
        }
    }
}