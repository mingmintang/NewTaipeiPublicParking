package com.mingmin.newtaipeipublicparking.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.mingmin.newtaipeipublicparking.db.ParkingDAO
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
        fun updateAndReadRecordsFromDb(parkingDAO: ParkingDAO, disposables: CompositeDisposable): Task<ArrayList<Record>> {
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
                        source.setException(Exception("json data is null"))
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
    }
}