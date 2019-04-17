package com.mingmin.newtaipeipublicparking.data.local

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mingmin.newtaipeipublicparking.AndroidTestUtils.parkingLot1
import com.mingmin.newtaipeipublicparking.AndroidTestUtils.parkingLot2
import com.mingmin.newtaipeipublicparking.AndroidTestUtils.parkingLots
import org.hamcrest.Matchers.hasItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParkingLotLocalRepositoryTest {
    private val area = "板橋區"
    private val keyword = "停車場"

    private lateinit var parkingLotDao: ParkingLotDao

    @Before
    fun setup() {
        parkingLotDao = ParkingLotDao.newInstance(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test
    fun saveAndGetParkingLots() {
        // clean database
        parkingLotDao.deleteAll()
        assertEquals(0, parkingLotDao.count())

        // insert parkingLots
        parkingLotDao.insertAll(parkingLots)

        // query all parkingLots
        val allResults = parkingLotDao.queryAll()
        assertEquals(parkingLots.size, allResults.size)
        assertThat(allResults, hasItem(parkingLot1))
        assertThat(allResults, hasItem(parkingLot2))

        // query parkingLots by area and keyword
        val results = parkingLotDao.queryByAreaAndKeyword(area, keyword)
        assertEquals(1, results.size)
        assertThat(results, hasItem(parkingLot1))

        // clean database
        parkingLotDao.deleteAll()
    }
}