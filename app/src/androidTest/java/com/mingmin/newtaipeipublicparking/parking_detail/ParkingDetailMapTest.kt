package com.mingmin.newtaipeipublicparking.parking_detail

import android.Manifest
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.IdlingResource
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import com.mingmin.newtaipeipublicparking.R
import com.mingmin.newtaipeipublicparking.data.ParkingLot
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ParkingDetailLoadingTest {
    private val parkingLot = ParkingLot(1, 10056, "板橋區", "遠東百貨停車場", 2,
        "立體式建築附設停車空間", "板橋區中山路一段152號", "", "小型車計時60元;",
        "0~24時", 296882.0, 2767068.0, 453, 0, 0)
    private lateinit var countingIdlingResource: IdlingResource

    @Rule
    @JvmField
    val permissionRule = GrantPermissionRule.grant("android.location.GPS_ENABLED_CHANGE")

    @Rule
    @JvmField
    val parkingDetailTestRule =
        object : ActivityTestRule<ParkingDetailActivity>(ParkingDetailActivity::class.java) {
        override fun getActivityIntent(): Intent {
            val targetContext = InstrumentationRegistry.getTargetContext()
            val intent = Intent(targetContext, ParkingDetailActivity::class.java)
            intent.putExtra("ParkingLot", parkingLot)
            return intent
        }
    }

    @Before
    fun setup() {
//        countingIdlingResource = parkingDetailTestRule.activity.getCountingIdlingResource()
//        IdlingRegistry.getInstance().register(countingIdlingResource)
    }

    @After
    fun clear() {
//        IdlingRegistry.getInstance().unregister(countingIdlingResource)
    }

    @Test
    fun loadMap_showMyLocationAndRoutes() {
        countingIdlingResource = parkingDetailTestRule.activity.getCountingIdlingResource()
        IdlingRegistry.getInstance().register(countingIdlingResource)
        onView(withId(R.id.routes)).check(matches(isDisplayed()))
        IdlingRegistry.getInstance().unregister(countingIdlingResource)

        onView(withId(R.id.routes)).perform(click())
        IdlingRegistry.getInstance().register(countingIdlingResource)
//        onView()
//        IdlingRegistry.getInstance().unregister(countingIdlingResource)

        Thread.sleep(3000)
        val intent = Intent("android.location.GPS_ENABLED_CHANGE")
        intent.putExtra("enabled", false)
        InstrumentationRegistry.getTargetContext().sendBroadcast(intent)
    }
}