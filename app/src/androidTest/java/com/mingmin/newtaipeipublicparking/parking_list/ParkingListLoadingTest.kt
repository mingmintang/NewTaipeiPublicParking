package com.mingmin.newtaipeipublicparking.parking_list

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.recyclerview.widget.RecyclerView
import com.mingmin.newtaipeipublicparking.R
import com.mingmin.newtaipeipublicparking.RecyclerViewActionsExtension.Companion.itemAtPosition
import org.hamcrest.Matchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParkingListLoadingTest {
    private val databaseName = "parking.db"
    private val parkingLotSubString = "停車場"

    @Rule
    @JvmField
    val parkingListTestRule =
        ActivityTestRule(ParkingListActivity::class.java, false, false)

    private val launchActivity = LaunchActivity()

    @Test
    fun loadParkingList_firstAndSecondLaunch() {
        InstrumentationRegistry.getInstrumentation().targetContext.deleteDatabase(databaseName)
        // First launch: download all data to local and load from local
        launchActivity.open()
        checkDataLoadSuccess()
        launchActivity.close()

        // Second launch: load from local.
        launchActivity.open()
        checkDataLoadSuccess()
        launchActivity.close()
    }

    /**
     * Total count of NewTaipei public parking lot is about 930.
     * Check items at 0, 400, 900 position to confirm if data is load success.
     */
    private fun checkDataLoadSuccess() {
        Espresso.onView(withId(R.id.parking_list))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
            .check(itemAtPosition(0)
                .onChildView(withId(R.id.parking_name))
                .matches(withText(containsString(parkingLotSubString))))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(400))
            .check(itemAtPosition(400)
                .onChildView(withId(R.id.parking_name))
                .matches(withText(containsString(parkingLotSubString))))
            .perform(scrollToPosition<RecyclerView.ViewHolder>(900))
            .check(itemAtPosition(900)
                .onChildView(withId(R.id.parking_name))
                .matches(withText(containsString(parkingLotSubString))))
    }

    private inner class LaunchActivity {
        private lateinit var idlingResource: IdlingResource

        fun open() {
            parkingListTestRule.launchActivity(null)
            idlingResource = parkingListTestRule.activity.getCountingIdlingResource()
            IdlingRegistry.getInstance().register(idlingResource)
        }

        fun close() {
            IdlingRegistry.getInstance().unregister(idlingResource)
            parkingListTestRule.finishActivity()
        }
    }
}