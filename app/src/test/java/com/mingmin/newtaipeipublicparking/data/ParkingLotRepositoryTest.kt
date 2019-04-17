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
import com.mingmin.newtaipeipublicparking.TestUtils.parkingLots

class ParkingLotRepositoryTest {
    private val area = "板橋"
    private val keyword = "遠東"
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