package com.mingmin.newtaipeipublicparking

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.mingmin.newtaipeipublicparking.adapter.ParkingRecyclerViewAdapter
import com.mingmin.newtaipeipublicparking.db.Databases
import com.mingmin.newtaipeipublicparking.db.ParkingDAO
import com.mingmin.newtaipeipublicparking.http.Downloads
import com.mingmin.newtaipeipublicparking.http.Record
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ParkingRecyclerViewAdapter.ItemClickListener {
    private val disposables = CompositeDisposable()

    private val parkingDao: ParkingDAO by lazy {
        ParkingDAO(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        readRecords()
    }

    private fun readRecords() {
        showLoading()
        if (parkingDao.count() > 0) {
            readRecordsFromDb()
        } else {
            updateAndReadRecordsFromDb()
        }
    }

    private fun setupParkingList(records: ArrayList<Record>) {
        showParkingList()
        main_parking_list.adapter = ParkingRecyclerViewAdapter(records, this)
    }

    private fun showParkingList() {
        main_loading.visibility = View.GONE
        main_empty.visibility = View.GONE
        main_parking_list.visibility = View.VISIBLE
    }

    private fun showEmptyInfo() {
        main_loading.visibility = View.GONE
        main_empty.visibility = View.VISIBLE
        main_parking_list.visibility = View.GONE
    }

    private fun showLoading() {
        main_loading.visibility = View.VISIBLE
        main_empty.visibility = View.GONE
        main_parking_list.visibility = View.GONE
    }

    private fun readRecordsFromDb() {
        Databases.readRecordsFromDb(parkingDao, disposables)
            .addOnSuccessListener { setupParkingList(it) }
            .addOnFailureListener { println(it); showEmptyInfo() }
    }

    private fun updateAndReadRecordsFromDb() {
        Downloads.updateAndReadRecordsFromDb(parkingDao, disposables)
            .addOnSuccessListener { setupParkingList(it) }
            .addOnFailureListener { println(it); showEmptyInfo() }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
        parkingDao.close()
    }

    override fun onParkingItemClick(record: Record) {
        val intent = Intent(applicationContext, DetailActivity::class.java)
        intent.putExtra("record", record)
        startActivity(intent)
    }
}
