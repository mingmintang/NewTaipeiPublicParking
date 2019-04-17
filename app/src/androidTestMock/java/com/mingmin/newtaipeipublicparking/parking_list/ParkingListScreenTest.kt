package com.mingmin.newtaipeipublicparking.parking_list

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiScrollable
import androidx.test.uiautomator.UiSelector
import com.mingmin.newtaipeipublicparking.AndroidTestUtils.getParkingLotFromRecyclerView
import com.mingmin.newtaipeipublicparking.R
import com.mingmin.newtaipeipublicparking.RecyclerViewActionsExtension.Companion.itemAtPosition
import com.mingmin.newtaipeipublicparking.data.ParkingLot
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ParkingListScreenTest {
    private val area = "新莊區"
    private val keyword = "新莊"

    @Rule
    @JvmField
    val parkingListTestRule = ActivityTestRule(ParkingListActivity::class.java)

    @Rule
    @JvmField
    val permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    private val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private lateinit var countingIdlingResource: IdlingResource

    @Before
    fun setup() {
        countingIdlingResource = parkingListTestRule.activity.getCountingIdlingResource()
        IdlingRegistry.getInstance().register(countingIdlingResource)
    }

    @After
    fun clear() {
        IdlingRegistry.getInstance().unregister(countingIdlingResource)
    }

    @Test
    fun selectArea_loadParkingListByArea() {
        onView(withId(R.id.area_spinner)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`(area))).perform(click())

        onView(withId(R.id.parking_list))
            .check(itemAtPosition(0)
                .onChildView(withId(R.id.parking_area))
                .matches(withText(area))
            )
    }

    @Test
    fun inputKeyword_loadParkingListByKeyword() {
        onView(withId(R.id.keyword_input)).perform(replaceText(keyword))
        onView(withId(R.id.keyword_button)).perform(click())

        onView(withId(R.id.parking_list))
            .check(itemAtPosition(0)
                .onChildView(withId(R.id.parking_name))
                .matches(withText(containsString(keyword)))
            )
    }

    @Test
    fun selectAreaAndInputKeyword_loadParkingListByAreaAndKeyword() {
        onView(withId(R.id.area_spinner)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`(area))).perform(click())
        onView(withId(R.id.keyword_input)).perform(replaceText(keyword))
        onView(withId(R.id.keyword_button)).perform(click())

        onView(withId(R.id.parking_list))
            .check(itemAtPosition(0)
                .onChildView(withId(R.id.parking_name))
                .matches(withText(containsString(keyword)))
                .onChildView(withId(R.id.parking_area))
                .matches(withText(area))
            )
    }

    @Test
    fun clickListItem_openParkingLotDetail() {
        val parkingLot = scrollToPositionOfRecyclerView(R.id.parking_list, 100)
        onView(withId(R.id.parking_list)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(100, click()))

        onView(withId(R.id.toolbar)).check(matches(withChild(withText(parkingLot.NAME))))
        onView(withId(R.id.parking_name)).check(matches(withText(parkingLot.NAME)))
        onView(withId(R.id.area)).check(matches(withText(parkingLot.AREA)))
        onView(withId(R.id.servicetime)).check(matches(withText(parkingLot.SERVICETIME)))
        onView(withId(R.id.address)).check(matches(withText(parkingLot.ADDRESS)))
    }

    @Test
    fun rotateScreen_keepPositionOfList() {
        val parkingList = UiScrollable(UiSelector().resourceId("com.mingmin.newtaipeipublicparking.mock:id/parking_list"))
        parkingList.scrollToEnd(2)
        onView(withText("行政中心公有停車場")).check(matches(isDisplayed()))
        uiDevice.setOrientationRight()
        onView(withText("行政中心公有停車場")).check(matches(isDisplayed()))
        uiDevice.setOrientationNatural()
        onView(withText("行政中心公有停車場")).check(matches(isDisplayed()))
    }

    private fun scrollToPositionOfRecyclerView(recyclerViewId: Int, position: Int): ParkingLot {
        val parkingLot = getParkingLotFromRecyclerView(recyclerViewId, position)
        onView(withId(recyclerViewId)).perform(scrollToPosition<RecyclerView.ViewHolder>(position))
        return parkingLot
    }
}