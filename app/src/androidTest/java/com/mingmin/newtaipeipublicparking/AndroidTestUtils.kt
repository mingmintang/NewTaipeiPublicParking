package com.mingmin.newtaipeipublicparking

import android.support.test.espresso.Espresso
import android.support.test.espresso.PerformException
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.util.HumanReadables
import android.support.v7.widget.RecyclerView
import android.view.View
import com.mingmin.newtaipeipublicparking.data.ParkingLot
import com.mingmin.newtaipeipublicparking.parking_list.ParkingListRecyclerViewAdapter
import org.hamcrest.Matcher
import org.hamcrest.Matchers

object TestUtils {
    val parkingLot1 = ParkingLot(10056, "板橋區", "遠東百貨停車場", 2,
        "立體式建築附設停車空間", "板橋區中山路一段152號", "", "小型車計時60元;",
        "0~24時", 296882.0, 2767068.0, 453, 0, 0)
    val parkingLot2 = ParkingLot(50009, "新莊區", "新莊文化藝術中心地下停車場", 1,
        "立體式多目標附建停車場", "新北市新莊區中平路133號B1", "22778246", "小型車計時30元;小型車計時10元;小型車月租3000元;",
        "0~24時", 294980.942808826, 2771127.21701325, 65, 0, 0)
    val parkingLots = listOf(parkingLot1, parkingLot2)

    fun getParkingLotFromRecyclerView(recyclerViewId: Int, position: Int): ParkingLot {
        lateinit var parkingLot: ParkingLot

        Espresso.onView(withId(recyclerViewId)).perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return Matchers.allOf(
                    ViewMatchers.isAssignableFrom(RecyclerView::class.java),
                    ViewMatchers.isDisplayed()
                )
            }
            override fun getDescription(): String {
                return "getParkingLotFromRecyclerView get data at position: $position"
            }

            override fun perform(uiController: UiController?, view: View?) {
                val recyclerView = view as RecyclerView
                recyclerView.scrollToPosition(position)
                uiController?.loopMainThreadUntilIdle()

                val holder = recyclerView.findViewHolderForAdapterPosition(position)
                if (holder == null) {
                    throw PerformException.Builder()
                        .withActionDescription(this.toString())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(IllegalStateException("No view holder at position: $position"))
                        .build()
                } else {
                    @Suppress("UNCHECKED_CAST")
                    parkingLot = (holder as ParkingListRecyclerViewAdapter.ViewHolder).parkingLot
                }
            }
        })
        return parkingLot
    }
}