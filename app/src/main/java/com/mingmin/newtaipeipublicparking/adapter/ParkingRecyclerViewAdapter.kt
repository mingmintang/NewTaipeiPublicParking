package com.mingmin.newtaipeipublicparking.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mingmin.newtaipeipublicparking.R
import com.mingmin.newtaipeipublicparking.http.Record

class ParkingRecyclerViewAdapter(val records: ArrayList<Record>, val listener: ItemClickListener)
    : RecyclerView.Adapter<ParkingRecyclerViewAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.item_parking_name)
        val area = itemView.findViewById<TextView>(R.id.item_parking_area)
        val servicetime = itemView.findViewById<TextView>(R.id.item_parking_servicetime)
        val address = itemView.findViewById<TextView>(R.id.item_parking_address)

        init {
            itemView.setOnClickListener {
                val record = it.tag as Record
                listener.onParkingItemClick(record)
            }
        }
    }

    interface ItemClickListener {
        fun onParkingItemClick(record: Record)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_parking, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return records.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]
        with(holder) {
            name.text = record.NAME
            area.text = record.AREA
            servicetime.text = record.SERVICETIME
            address.text = record.ADDRESS
            itemView.tag = record
        }
    }
}