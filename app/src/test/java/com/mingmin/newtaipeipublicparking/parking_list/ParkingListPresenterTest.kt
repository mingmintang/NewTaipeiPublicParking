package com.mingmin.newtaipeipublicparking.parking_list

import com.mingmin.newtaipeipublicparking.TestUtils.parkingLot1
import com.mingmin.newtaipeipublicparking.TestUtils.parkingLots
import com.mingmin.newtaipeipublicparking.data.ParkingLotRepository
import com.mingmin.newtaipeipublicparking.update_all_data.ForegroundService
import com.mingmin.newtaipeipublicparking.util.schedulers.ImmediateSchedulerProvider
import com.mingmin.newtaipeipublicparking.util.schedulers.SchedulerProvider
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.mockk.verifyOrder
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.junit.After
import org.junit.Before
import org.junit.Test

class ParkingListPresenterTest {
    private val area = "板橋"
    private val keyword = "遠東"
    private lateinit var disposables: CompositeDisposable
    private lateinit var schedulerProvider: SchedulerProvider
    private lateinit var presenter: ParkingListPresenter

    @MockK
    private lateinit var parkingLotRepository: ParkingLotRepository
    @MockK
    private lateinit var parkingListView: ParkingListContract.View
    @MockK
    private lateinit var foregroundService: ForegroundService

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        disposables = CompositeDisposable()
        schedulerProvider = ImmediateSchedulerProvider()

        presenter = ParkingListPresenter(disposables,
            schedulerProvider, parkingLotRepository, parkingListView, foregroundService)
    }

    @After
    fun clear() {
        disposables.clear()
    }

    @Test
    fun loadParkingList_forceUpdate_startUpdateAllDataService() {
        presenter.loadParkingList(true, area, keyword)

        verifyOrder {
            parkingListView.showLoading()
            foregroundService.startUpdateAllData()
        }
    }

    @Test
    fun loadParkingList_localIsEmpty_startUpdateAllDataService() {
        every { parkingLotRepository.isLocalParkingLotsEmpty() } returns Single.just(true)

        presenter.loadParkingList(false, area, keyword)

        verifyOrder {
            parkingListView.showLoading()
            foregroundService.startUpdateAllData()
        }
    }

    @Test
    fun loadParkingList_localLoadSuccess_showParkingList() {
        every { parkingLotRepository.isLocalParkingLotsEmpty() } returns Single.just(false)
        every { parkingLotRepository.getLocalParkingLots(any(), any()) } returns Single.just(parkingLots)

        presenter.loadParkingList(false, area, keyword)

        verifyOrder {
            parkingListView.showLoading()
            parkingListView.showParkingList(parkingLots)
        }
    }

    @Test
    fun loadParkingList_localLoadFail_showEmptyInfo() {
        every { parkingLotRepository.isLocalParkingLotsEmpty() } returns Single.just(false)
        every { parkingLotRepository.getLocalParkingLots(area, keyword) } returns Single.error(Throwable())

        presenter.loadParkingList(false, area, keyword)

        verifyOrder {
            parkingListView.showLoading()
            parkingListView.showEmptyInfo()
        }
    }

    @Test
    fun openParkingLotDetail_showDetail() {
        presenter.openParkingLotDetail(parkingLot1)

        verify(exactly = 1) { parkingListView.showParkingLotDetail(parkingLot1) }
    }
}