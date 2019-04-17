package com.mingmin.newtaipeipublicparking.parking_list

import com.mingmin.newtaipeipublicparking.data.ParkingLot
import com.mingmin.newtaipeipublicparking.data.ParkingLotRepository
import com.mingmin.newtaipeipublicparking.update_all_data.ForegroundService
import com.mingmin.newtaipeipublicparking.util.EspressoIdlingResource
import com.mingmin.newtaipeipublicparking.util.schedulers.SchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver

class ParkingListPresenter(
    private val disposables: CompositeDisposable,
    private val schedulerProvider: SchedulerProvider,
    private val parkingLotRepository: ParkingLotRepository,
    private val parkingListView: ParkingListContract.View,
    private val foregroundService: ForegroundService
) : ParkingListContract.ActionsListener {

    override fun loadParkingList(forceUpdate: Boolean, area: String?, keyword: String?) {
        parkingListView.showLoading()
        if (forceUpdate) {
            updateAllData()
            return
        }

        EspressoIdlingResource.increment()
        disposables.clear()
        disposables.add(
            parkingLotRepository.isLocalParkingLotsEmpty()
                .subscribeOn(schedulerProvider.io())
                .flatMap { isEmpty ->
                    if (isEmpty) {
                        Single.error(Throwable(ERR_LOCAL_PARKINGLOT_EMPTY))
                    } else {
                        parkingLotRepository.getLocalParkingLots(area, keyword)
                    }
                }.observeOn(schedulerProvider.ui())
                .subscribeWith(object : DisposableSingleObserver<List<ParkingLot>>() {
                    override fun onSuccess(parkingLots: List<ParkingLot>) {
                        parkingListView.showParkingList(parkingLots)
                        EspressoIdlingResource.decrement()
                    }
                    override fun onError(e: Throwable) {
                        if (e.message == ERR_LOCAL_PARKINGLOT_EMPTY) {
                            EspressoIdlingResource.clear()
                            updateAllData()
                        } else {
                            parkingListView.showEmptyInfo()
                            EspressoIdlingResource.decrement()
                        }
                    }
                })
        )
    }

    private fun updateAllData() {
        foregroundService.startUpdateAllData()
    }

    override fun openParkingLotDetail(parkingLot: ParkingLot) {
        parkingListView.showParkingLotDetail(parkingLot)
    }

    companion object {
        private const val ERR_LOCAL_PARKINGLOT_EMPTY: String = "ParkingLot local data is empty"
    }
}