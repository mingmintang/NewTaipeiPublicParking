package com.mingmin.newtaipeipublicparking.parking_list

import com.mingmin.newtaipeipublicparking.data.ParkingLot
import com.mingmin.newtaipeipublicparking.data.ParkingLotRepository

class ParkingListPresenter(
    private val parkingLotRepository: ParkingLotRepository,
    private val parkingListView: ParkingListContract.View)
    : ParkingListContract.ActionsListener, ParkingLotRepository.LoadListener {

    init {
        parkingLotRepository.setLoadListener(this)
    }

    override fun loadParkingList(forceUpdate: Boolean, area: String?, keyword: String?) {
        parkingListView.showLoading()
        parkingLotRepository.getParkingLots(forceUpdate, area, keyword)
    }

    override fun onParkingLotsLoadSuccess(parkingLots: List<ParkingLot>) {
        parkingListView.showParkingList(parkingLots)
    }

    override fun onParkingLotsLoadFail() {
        parkingListView.showEmptyInfo()
    }
}