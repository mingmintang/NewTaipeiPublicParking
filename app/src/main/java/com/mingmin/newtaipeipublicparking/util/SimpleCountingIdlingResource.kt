package com.mingmin.newtaipeipublicparking.util

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback
import java.util.concurrent.atomic.AtomicInteger

class SimpleCountingIdlingResource(private val resourceName: String) : IdlingResource {
    private val counter = AtomicInteger(0)
    @Volatile private var resourceCallback: ResourceCallback? = null

    override fun getName(): String = resourceName

    override fun isIdleNow(): Boolean { return counter.get() == 0 }

    override fun registerIdleTransitionCallback(callback: ResourceCallback?) {
        resourceCallback = callback
    }

    fun increment() { counter.getAndIncrement() }

    fun decrement() {
        val value = counter.decrementAndGet()
        if (value == 0) {
            resourceCallback?.onTransitionToIdle()
        }
        if (value < 0) {
            throw IllegalStateException("Counter has been corrupted!")
        }
    }

    fun clear() {
        counter.getAndSet(0)
    }
}