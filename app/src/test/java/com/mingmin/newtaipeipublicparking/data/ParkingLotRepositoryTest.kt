package com.mingmin.newtaipeipublicparking.data

import com.mingmin.newtaipeipublicparking.data.local.ParkingLotDao
import com.mingmin.newtaipeipublicparking.data.remote.ParkingLotService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.mockk.verifyOrder
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test

class ParkingLotRepositoryImplTest {
    private val area = "板橋"
    private val keyword = "遠東"
    private val parkingLots = listOf<ParkingLot>(
        ParkingLot(1, 10056, "板橋區", "遠東百貨停車場", 2, "立體式建築附設停車空間", "板橋區中山路一段152號", "02-3346773", "小型車計時60元;", "0~24時", 296882.0, 2767068.0, 453, 0, 0),
        ParkingLot(1, 30034, "中和區", "南華停車場", 2, "平面式臨時路外停車場", "中和區中和路281號邊", "02-1110234", "小型車計時20元;小型車月租3500元;", "6~24時", 301086.0, 2765825.0, 42, 0, 0)
    )
    private lateinit var repository: ParkingLotRepositoryImpl

    @MockK
    private lateinit var parkingLotDao: ParkingLotDao
    @MockK
    private lateinit var parkingLotService: ParkingLotService

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        repository = ParkingLotRepositoryImpl(parkingLotDao, parkingLotService)
    }

    @Test
    fun isLocalParkingLotsEmpty_lessThanZeroIsTrue() {
        every { parkingLotDao.count() } returns -1
        val testObserver = TestObserver<Boolean>()

        repository.isLocalParkingLotsEmpty().subscribe(testObserver)

        verify(exactly = 1) { parkingLotDao.count() }
        testObserver.assertValue(true)
    }

    @Test
    fun isLocalParkingLotsEmpty_equalToZeroIsTrue() {
        every { parkingLotDao.count() } returns 0
        val testObserver = TestObserver<Boolean>()

        repository.isLocalParkingLotsEmpty().subscribe(testObserver)

        verify(exactly = 1) { parkingLotDao.count() }
        testObserver.assertValue(true)
    }

    @Test
    fun isLocalParkingLotsEmpty_moreThanZeroIsFalse() {
        every { parkingLotDao.count() } returns 1
        val testObserver = TestObserver<Boolean>()

        repository.isLocalParkingLotsEmpty().subscribe(testObserver)

        verify(exactly = 1) { parkingLotDao.count() }
        testObserver.assertValue(false)
    }

    @Test
    fun getLocalParkingLots() {
        every { parkingLotDao.queryByAreaAndKeyword(any(), any()) } returns parkingLots
        val testObserver = TestObserver<List<ParkingLot>>()

        repository.getLocalParkingLots(area, keyword).subscribe(testObserver)

        verify(exactly = 1) { parkingLotDao.queryByAreaAndKeyword(area, keyword) }
        testObserver.assertValue(parkingLots)
    }

    @Test
    fun getAndSaveRemoteParkingLots() {
        every { parkingLotService.getAllParkingLots() } returns Single.just(parkingLots)
        val testObserver = TestObserver<List<ParkingLot>>()

        repository.getAndSaveRemoteParkingLots().subscribe(testObserver)

        verifyOrder {
            parkingLotService.getAllParkingLots()
            parkingLotDao.updateAll(parkingLots)
        }
        testObserver.assertValue(parkingLots)
    }
}