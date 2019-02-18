package com.mingmin.newtaipeipublicparking.parking_list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mingmin.newtaipeipublicparking.R
import com.mingmin.newtaipeipublicparking.data.ParkingLot

class ParkingListRecyclerViewAdapter(val parkingLots: ArrayList<ParkingLot>, val listener: ItemClickListener)
    : RecyclerView.Adapter<ParkingListRecyclerViewAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.parking_name)!!
        val area = itemView.findViewById<TextView>(R.id.parking_area)!!
        val servicetime = itemView.findViewById<TextView>(R.id.parking_servicetime)!!
        val address = itemView.findViewById<TextView>(R.id.parking_address)!!

        init {
            itemView.setOnClickListener {
                listener.onParkingItemClick(parkingLots[adapterPosition])
            }
        }
    }

    interface ItemClickListener {
        fun onParkingItemClick(parkingLot: ParkingLot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_parking, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return parkingLots.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val parkingLot = parkingLots[position]
        with(holder) {
            name.text = parkingLot.NAME
            area.text = parkingLot.AREA
            servicetime.text = parkingLot.SERVICETIME
            address.text = parkingLot.ADDRESS
        }
    }
}