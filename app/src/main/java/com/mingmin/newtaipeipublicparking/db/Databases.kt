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
        fun readRecordsFromDb(parkingDAO: ParkingDAO, disposables: CompositeDisposable): Task<ArrayList<Record>> {
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
                            source.setException(Exception("query records is empty"))
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