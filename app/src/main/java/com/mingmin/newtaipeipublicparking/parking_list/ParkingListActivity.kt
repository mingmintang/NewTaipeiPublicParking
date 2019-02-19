package com.mingmin.newtaipeipublicparking.parking_list

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.mingmin.newtaipeipublicparking.R
import com.mingmin.newtaipeipublicparking.data.ParkingLot
import com.mingmin.newtaipeipublicparking.data.ParkingLotRepository
import com.mingmin.newtaipeipublicparking.data.ParkingLotRepositoryImpl
import com.mingmin.newtaipeipublicparking.db.ParkingLotDao
import com.mingmin.newtaipeipublicparking.db.ParkingLotDaoImpl
import com.mingmin.newtaipeipublicparking.parking_detail.ParkingDetailActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_parking_list.*

class ParkingListActivity : AppCompatActivity(),
    ParkingListContract.View, ParkingListRecyclerViewAdapter.ItemClickListener {
    private val dispoables = CompositeDisposable()
    private lateinit var parkingLotDao: ParkingLotDao
    private lateinit var parkingLotRepository: ParkingLotRepository
    private lateinit var presenter: ParkingListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_list)

        parkingLotDao = ParkingLotDaoImpl(applicationContext)
        parkingLotRepository = ParkingLotRepositoryImpl(parkingLotDao, dispoables)
        presenter = ParkingListPresenter(parkingLotRepository, this)

        setupAreaSpinner()
        setupSwipeRefresh()
    }

    override fun onStart() {
        super.onStart()
        search_input.clearFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        dispoables.clear()
        parkingLotDao.close()
    }

    private fun setupAreaSpinner() {
        area_spinner.adapter = ArrayAdapter.createFromResource(
            applicationContext,
            R.array.new_taipei_areas,
            android.R.layout.simple_list_item_1
        )
        area_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                parent?.tag?.let {
                    val notRead = it as String
                    parent.tag = null
                    if (notRead == "not_read") return
                }
                loadParkingList(false)
            }
        }
    }

    private fun setupSwipeRefresh() {
        parking_list_container.setDistanceToTriggerSync(900)
        parking_list_container.setOnRefreshListener {
            updateAllData()
            parking_list_container.isRefreshing = false
        }

        empty_info_container.setOnRefreshListener {
            updateAllData()
            empty_info_container.isRefreshing = false
        }
    }

    private fun loadParkingList(forceUpdate: Boolean) {
        val area: String? = if (area_spinner.selectedItemPosition > 0) {
            area_spinner.selectedItem.toString()
        } else {
            null
        }
        val keyword: String? = if (search_input.text.isNullOrEmpty()) {
            null
        } else {
            search_input.text.toString()
        }
        presenter.loadParkingList(forceUpdate, area, keyword)
    }

    private fun updateAllData() {
        search_input.text?.clear()
        area_spinner.tag = "not_read"
        area_spinner.setSelection(0)
        loadParkingList(true)
    }

    override fun showParkingList(parkingLots: List<ParkingLot>) {
        parking_list.adapter = ParkingListRecyclerViewAdapter(
            ArrayList(parkingLots),
            this
        )
        switchListState(ListState.LIST)
        enableControllerWidgets(true)
    }

    override fun showLoading() {
        switchListState(ListState.LOADING)
        search_input.clearFocus()
        enableControllerWidgets(false)
    }

    override fun showEmptyInfo() {
        switchListState(ListState.EMPTY)
        enableControllerWidgets(true)
    }

    enum class ListState {
        LIST,
        EMPTY,
        LOADING
    }

    private fun switchListState(state: ListState) {
        when (state) {
            ListState.LIST -> {
                parking_list_container.visibility = View.VISIBLE
                loading.visibility = View.GONE
                empty_info_container.visibility = View.GONE
            }
            ListState.EMPTY -> {
                parking_list_container.visibility = View.GONE
                loading.visibility = View.GONE
                empty_info_container.visibility = View.VISIBLE
            }
            ListState.LOADING -> {
                parking_list_container.visibility = View.GONE
                loading.visibility = View.VISIBLE
                empty_info_container.visibility = View.GONE
            }
        }
    }

    private fun enableControllerWidgets(isEnabled: Boolean) {
        area_spinner.isEnabled = isEnabled
        search_button.isEnabled = isEnabled
        search_input.isEnabled = isEnabled
    }

    fun onSearchButtonClick(view: View) {
        loadParkingList(false)
    }

    override fun onParkingItemClick(parkingLot: ParkingLot) {
        val intent = Intent(applicationContext, ParkingDetailActivity::class.java)
        intent.putExtra("ParkingLot", parkingLot)
        startActivity(intent)
    }
}
