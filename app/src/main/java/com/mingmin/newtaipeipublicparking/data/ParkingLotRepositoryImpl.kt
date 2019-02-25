package com.mingmin.newtaipeipublicparking.data

import com.mingmin.newtaipeipublicparking.platform_model.PlatformModel
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableMaybeObserver
import io.reactivex.schedulers.Schedulers

class ParkingLotRepositoryImpl(
    private val platformModel: PlatformModel,
    private val disposables: CompositeDisposable)
    : ParkingLotRepository {

    private val parkingLotDao = platformModel.getParkingLotDao()
    private var loadListener: ParkingLotRepository.LoadListener? = null

    override fun getParkingLots(forceUpdate: Boolean, area: String?, keyword: String?) {
        if (forceUpdate) {
            updateAll()
            return
        }
        val operation = { parkingLotDao.count() }
        callDbOperationByRxJava(disposables, operation, object : DisposableMaybeObserver<Int>() {
            override fun onSuccess(count: Int) {
                if (count > 0) getParkingLotsFromDb(area, keyword) else updateAll()
            }
            override fun onComplete() {}
            override fun onError(e: Throwable) { println(e) }
        })
    }

    private fun updateAll() {
        platformModel.startUpdateAllDataForegroundService()
    }

    override fun setLoadListener(listener: ParkingLotRepository.LoadListener) {
        loadListener = listener
    }

    private fun getParkingLotsFromDb(area: String?, keyword: String?) {
        val operation = {
            parkingLotDao.queryByAreaAndKeyword(area, keyword)
        }
        callDbOperationByRxJava(disposables, operation, LoadParkingLotsObserver())
    }

    private inner class LoadParkingLotsObserver : DisposableMaybeObserver<ArrayList<ParkingLot>?>() {
        override fun onSuccess(parkingLots: ArrayList<ParkingLot>) {
            loadListener?.onParkingLotsLoadSuccess(parkingLots)
        }
        override fun onComplete() {
            loadListener?.onParkingLotsLoadFail()
        }
        override fun onError(e: Throwable) {
            loadListener?.onParkingLotsLoadFail()
        }
    }

    private fun <TResult> callDbOperationByRxJava(
        disposables: CompositeDisposable,
        operation: () -> TResult,
        observer: DisposableMaybeObserver<TResult>) {
        disposables.add(
            Maybe.fromCallable(operation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer)
        )
    }
}