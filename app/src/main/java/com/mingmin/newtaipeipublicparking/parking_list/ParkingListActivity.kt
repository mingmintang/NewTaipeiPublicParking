package com.mingmin.newtaipeipublicparking.parking_list

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.IdlingResource
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.appcompat.app.AppCompatActivity
import com.mingmin.newtaipeipublicparking.R
import com.mingmin.newtaipeipublicparking.util.EspressoIdlingResource
import kotlinx.android.synthetic.main.fragment_parking_list.*

class ParkingListActivity : AppCompatActivity() {
    private var contentFragment: ParkingListFragment? = null
    private var broadcastReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_list)

        if (savedInstanceState == null) {
            setupFragment()
            registerBroadcastReceiver()
        }
    }

    override fun onStart() {
        super.onStart()
        keyword_input.clearFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        broadcastReceiver?.let { LocalBroadcastManager.getInstance(this).unregisterReceiver(it) }
    }

    private fun setupFragment() {
        contentFragment = ParkingListFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.contentFrame, contentFragment!!)
        transaction.commit()
    }

    private fun registerBroadcastReceiver() {
        broadcastReceiver = BroadcastReceiver()
        val filter = IntentFilter().apply {
            addAction(ACTION_UPDATE_ALL_DATA_SUCCESS)
            addAction(ACTION_UPDATE_ALL_DATA_FAIL)
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver!!,
            filter
        )
    }

    inner class BroadcastReceiver : android.content.BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.action?.let {
                when (it) {
                    ACTION_UPDATE_ALL_DATA_SUCCESS -> contentFragment?.loadParkingList()
                    ACTION_UPDATE_ALL_DATA_FAIL -> contentFragment?.showEmptyInfo()
                }
                return
            }
            contentFragment?.showEmptyInfo()
        }
    }

    companion object {
        const val ACTION_UPDATE_ALL_DATA_SUCCESS = "com.mingmin.newtaipeipublicparking.UPDATE_ALL_DATA_SUCCESS"
        const val ACTION_UPDATE_ALL_DATA_FAIL = "com.mingmin.newtaipeipublicparking.UPDATE_ALL_DATA_FAIL"
    }

    @VisibleForTesting
    fun getCountingIdlingResource(): IdlingResource {
        return EspressoIdlingResource.getIdlingResource()
    }
}
