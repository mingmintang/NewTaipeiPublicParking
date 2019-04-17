package com.mingmin.newtaipeipublicparking.util

import androidx.test.espresso.IdlingResource

object EspressoIdlingResource {
    private const val RESOURCE_NAME = "EspressoIdlingResource"
    private var countingIdlingResource = SimpleCountingIdlingResource(RESOURCE_NAME)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) countingIdlingResource.decrement()
    }

    fun clear() = countingIdlingResource.clear()

    fun getIdlingResource(): IdlingResource {
        return countingIdlingResource
    }
}