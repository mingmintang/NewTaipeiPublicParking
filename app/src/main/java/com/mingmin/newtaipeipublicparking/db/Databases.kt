package com.mingmin.newtaipeipublicparking.db

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.mingmin.newtaipeipublicparking.http.Record
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableMaybeObserver
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class Databases {
    companion object {
        private val QUERY_RECORDS_EMPTY = "query records is empty"

        fun readAllRecords(parkingDAO: ParkingDAO, disposables: CompositeDisposable): Task<ArrayList<Record>> {
            val source = TaskCompletionSource<ArrayList<Record>>()
            disposables.add(
                Maybe.fromCallable { parkingDAO.queryAll() }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableMaybeObserver<ArrayList<Record>>() {
                        override fun onSuccess(records: ArrayList<Record>) {
                            source.setResult(records)
                        }
                        override fun onComplete() {
                            source.setException(Exception(QUERY_RECORDS_EMPTY))
                        }
                        override fun onError(e: Throwable) {
                            source.setException(Exception(e))
                        }
                    })
            )
            return source.task
        }

        fun readRecordsByAreaAndKeyword(parkingDAO: ParkingDAO, disposables: CompositeDisposable,
                                        area: String?, keyword: String?)
            : Task<ArrayList<Record>> {
            val source = TaskCompletionSource<ArrayList<Record>>()
            disposables.add(
                Maybe.fromCallable { parkingDAO.queryByAreaAndKeyword(area, keyword) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableMaybeObserver<ArrayList<Record>>() {
                        override fun onSuccess(records: ArrayList<Record>) {
                            source.setResult(records)
                        }
                        override fun onComplete() {
                            source.setException(Exception(QUERY_RECORDS_EMPTY))
                        }
                        override fun onError(e: Throwable) {
                            source.setException(Exception(e))
                        }
                    })
            )
            return source.task
        }
    }
}