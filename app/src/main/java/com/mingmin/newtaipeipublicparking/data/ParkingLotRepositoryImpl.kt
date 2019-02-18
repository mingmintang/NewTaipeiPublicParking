package com.mingmin.newtaipeipublicparking.data

import com.mingmin.newtaipeipublicparking.db.ParkingLotDao
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableMaybeObserver
import io.reactivex.schedulers.Schedulers

class ParkingLotRepositoryImpl(
    private val parkingLotDao: ParkingLotDao,
    private val disposables: CompositeDisposable,
    private val parkingLotService: ParkingLotService)
    : ParkingLotRepository {

    private var loadListener: ParkingLotRepository.LoadListener? = null

    override fun getParkingLots(forceUpdate: Boolean, area: String?, keyword: String?) {
        if (forceUpdate || parkingLotDao.count() == 0) {
            updateAll()
        } else {
            getParkingLotsFromDb(area, keyword)
        }
    }

    private fun updateAll() {
        parkingLotService.getAllParkingLots { parkingLots ->
            val operation = {
                parkingLots?.let { parkingLotDao.updateAll(it) }
                parkingLotDao.queryAll()
            }
            callDbOperationByRxJava(disposables, operation, LoadParkingLotsObserver())
        }
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