package com.mingmin.newtaipeipublicparking.parking_list


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.mingmin.newtaipeipublicparking.Injection

import com.mingmin.newtaipeipublicparking.R
import com.mingmin.newtaipeipublicparking.data.ParkingLot
import com.mingmin.newtaipeipublicparking.data.ParkingLotRepository
import com.mingmin.newtaipeipublicparking.parking_detail.ParkingDetailActivity
import com.mingmin.newtaipeipublicparking.parking_list.ParkingListRecyclerViewAdapter.ItemClickListener
import com.mingmin.newtaipeipublicparking.update_all_data.AndroidForegroundService
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_parking_list.*


class ParkingListFragment : Fragment(), ParkingListContract.View {

    private lateinit var dispoables: CompositeDisposable
    private lateinit var parkingLotRepository: ParkingLotRepository
    private lateinit var presenter: ParkingListPresenter
    private var currentAreaPosition = -1

    private val itemClickListener = object : ItemClickListener {
        override fun onParkingItemClick(parkingLot: ParkingLot) {
            presenter.openParkingLotDetail(parkingLot)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_parking_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dispoables = CompositeDisposable()
        parkingLotRepository = Injection.provideParkingLotRepository(context!!)
        presenter = ParkingListPresenter(
            dispoables,
            Injection.provideSchedulerProvider(),
            parkingLotRepository,
            this,
            AndroidForegroundService(context!!)
        )

        setupSearchInput()
        setupSwipeRefresh()
        setupAreaSpinner()
    }

    override fun onStart() {
        super.onStart()
        keyword_input.clearFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dispoables.clear()
    }

    private fun setupAreaSpinner() {
        area_spinner.adapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.new_taipei_areas,
            android.R.layout.simple_list_item_1
        )
        area_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (currentAreaPosition == position) { return }
                currentAreaPosition = position
                loadParkingList()
            }
        }
    }

    private fun setupSearchInput() {
        keyword_button.setOnClickListener { loadParkingList() }
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

    fun loadParkingList(forceUpdate: Boolean = false) {
        val area: String? = if (area_spinner.selectedItemPosition > 0) {
            area_spinner.selectedItem.toString()
        } else {
            null
        }
        val keyword: String? = if (keyword_input.text.isNullOrEmpty()) {
            null
        } else {
            keyword_input.text.toString()
        }
        presenter.loadParkingList(forceUpdate, area, keyword)
    }

    private fun updateAllData() {
        keyword_input.text?.clear()
        currentAreaPosition = 0
        area_spinner.setSelection(0)
        presenter.loadParkingList(true, null, null)
    }

    override fun showParkingList(parkingLots: List<ParkingLot>) {
        parking_list.adapter = ParkingListRecyclerViewAdapter(
            ArrayList(parkingLots),
            itemClickListener
        )
        switchListState(ListState.LIST)
        enableControllerWidgets(true)
    }

    override fun showLoading() {
        switchListState(ListState.LOADING)
        keyword_input.clearFocus()
        enableControllerWidgets(false)
    }

    override fun showEmptyInfo() {
        switchListState(ListState.EMPTY)
        enableControllerWidgets(true)
    }

    override fun showParkingLotDetail(parkingLot: ParkingLot) {
        val intent = Intent(context, ParkingDetailActivity::class.java)
        intent.putExtra(ParkingDetailActivity.KEY_PARKING_LOT, parkingLot)
        startActivity(intent)
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
        keyword_button.isEnabled = isEnabled
        keyword_input.isEnabled = isEnabled
    }
}
