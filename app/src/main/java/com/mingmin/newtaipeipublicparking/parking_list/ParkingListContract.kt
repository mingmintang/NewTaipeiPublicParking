package com.mingmin.newtaipeipublicparking.parking_list

import com.mingmin.newtaipeipublicparking.data.ParkingLot

interface ParkingListContract {
    interface View {
        fun showParkingList(parkingLots: List<ParkingLot>)
        fun showLoading()
        fun showEmptyInfo()
        fun showParkingLotDetail(parkingLot: ParkingLot)
    }

    interface ActionsListener {
        fun loadParkingList(forceUpdate: Boolean, area: String?, keyword: String?)
        fun openParkingLotDetail(parkingLot: ParkingLot)
    }
}