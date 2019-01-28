package com.mingmin.newtaipeipublicparking

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.mingmin.newtaipeipublicparking.adapter.ParkingRecyclerViewAdapter
import com.mingmin.newtaipeipublicparking.db.Databases
import com.mingmin.newtaipeipublicparking.db.ParkingDAO
import com.mingmin.newtaipeipublicparking.http.Downloads
import com.mingmin.newtaipeipublicparking.http.Record
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ParkingRecyclerViewAdapter.ItemClickListener {
    private val disposables = CompositeDisposable()

    private val parkingDAO: ParkingDAO by lazy {
        ParkingDAO(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupAreaSpinner()
    }

    override fun onStart() {
        super.onStart()
        main_search_input.clearFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
        parkingDAO.close()
    }

    private fun setupAreaSpinner() {
        main_area_spinner.adapter = ArrayAdapter.createFromResource(applicationContext,
            R.array.new_taipei_areas,
            android.R.layout.simple_list_item_1)
        main_area_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                readRecords()
            }
        }
    }

    private fun readRecords() {
        main_search_input.clearFocus()
        showLoading()

        if (parkingDAO.count() > 0) {
            var area: String? = null
            var keyword: String? = null
            if (main_area_spinner.selectedItemPosition > 0) {
                area = main_area_spinner.selectedItem.toString()
            }
            main_search_input.text?.let {
                if (it.isNotEmpty()) {
                    keyword = main_search_input.text.toString()
                }
            }
            readRecordsFromDb(area, keyword)
        } else {
            updateAndReadAllRecordsFromDb()
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
        controllerComponentsEnabled(true)
    }

    private fun showEmptyInfo() {
        main_loading.visibility = View.GONE
        main_empty.visibility = View.VISIBLE
        main_parking_list.visibility = View.GONE
        controllerComponentsEnabled(true)
    }

    private fun showLoading() {
        main_loading.visibility = View.VISIBLE
        main_empty.visibility = View.GONE
        main_parking_list.visibility = View.GONE
        controllerComponentsEnabled(false)
    }

    private fun controllerComponentsEnabled(isEnabled: Boolean) {
        main_area_spinner.isEnabled = isEnabled
        main_search_button.isEnabled = isEnabled
        main_search_input.isEnabled = isEnabled
    }

    private fun updateAndReadAllRecordsFromDb() {
        Downloads.updateAndReadAllRecordsFromDb(parkingDAO, disposables)
            .addOnSuccessListener { setupParkingList(it) }
            .addOnFailureListener { println(it); showEmptyInfo() }
    }

    private fun readRecordsFromDb(area: String?, keyword: String?) {
        Databases.readRecordsByAreaAndKeyword(parkingDAO, disposables, area, keyword)
            .addOnSuccessListener { setupParkingList(it) }
            .addOnFailureListener { println(it); showEmptyInfo() }
    }

    override fun onParkingItemClick(record: Record) {
        val intent = Intent(applicationContext, DetailActivity::class.java)
        intent.putExtra("record", record)
        startActivity(intent)
    }

    fun onSearchButtonClick(view: View) {
        readRecords()
    }
}
