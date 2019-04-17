package com.mingmin.newtaipeipublicparking.parking_detail

import android.content.Context
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.mingmin.newtaipeipublicparking.AndroidTestUtils
import com.mingmin.newtaipeipublicparking.AndroidTestUtils.parkingLot1
import com.mingmin.newtaipeipublicparking.R
import com.mingmin.newtaipeipublicparking.parking_detail.ParkingDetailActivity.Companion.KEY_PARKING_LOT
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ParkingDetailMapTest {
    @Rule
    @JvmField
    val parkingDetailTestRule = ActivityTestRule(ParkingDetailActivity::class.java, false, false)
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val uiDevice = UiDevice.getInstance(instrumentation)
    private val launchActivity = LaunchActivity(parkingDetailTestRule, instrumentation.targetContext, uiDevice)

    @Test
    fun loadMap_allowLocationPermission_showMyLocation() {
        launchActivity.open()
        launchActivity.allowPermission(true)
        onView(withId(R.id.routes)).check(matches(isDisplayed()))
        val parkingLotMaker = uiDevice.findObject(UiSelector().descriptionContains(parkingLot1.NAME))
        parkingLotMaker.click()
        launchActivity.close()
    }

    @Test
    fun loadMap_DenyLocationPermission_NotShowMyLocation() {
        launchActivity.open()
        launchActivity.allowPermission(false)
        onView(withId(R.id.routes)).check(matches(not(isDisplayed())))
        val parkingLotMaker = uiDevice.findObject(UiSelector().descriptionContains(parkingLot1.NAME))
        parkingLotMaker.click()
        launchActivity.close()
    }

    private class LaunchActivity(val parkingDetailTestRule: ActivityTestRule<ParkingDetailActivity>,
                                 val context: Context,
                                 val uiDevice: UiDevice) {
        private lateinit var idlingResource: IdlingResource

        fun open() {
            val intent = Intent(context, ParkingDetailActivity::class.java)
            intent.putExtra(KEY_PARKING_LOT, parkingLot1)
            parkingDetailTestRule.launchActivity(intent)

            idlingResource = parkingDetailTestRule.activity.getCountingIdlingResource()
            IdlingRegistry.getInstance().register(idlingResource)
        }

        fun close() {
            IdlingRegistry.getInstance().unregister(idlingResource)
            parkingDetailTestRule.finishActivity()
        }

        fun allowPermission(isAllowed: Boolean) {
            AndroidTestUtils.allowPermission(uiDevice, isAllowed)
            if (isAllowed) {
                IdlingRegistry.getInstance().unregister(idlingResource)
                IdlingRegistry.getInstance().register(idlingResource)
            }
        }
    }
}