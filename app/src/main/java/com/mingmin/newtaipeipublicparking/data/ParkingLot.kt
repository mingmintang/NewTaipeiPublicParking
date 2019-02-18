package com.mingmin.newtaipeipublicparking.data

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties("_id")
data class ParkingLot(val _id: Long,
                      val ID: Int,
                      val AREA: String,
                      val NAME: String,
                      val TYPE: Int,
                      val SUMMARY: String,
                      val ADDRESS: String,
                      val TEL: String,
                      val PAYEX: String,
                      val SERVICETIME: String,
                      val TW97X: Double,
                      val TW97Y: Double,
                      val TOTALCAR: Int,
                      val TOTALMOTOR: Int,
                      val TOTALBIKE: Int
) : Parcelable