package com.mingmin.newtaipeipublicparking.http

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import kotlinx.android.parcel.Parcelize

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DownloadObject(val success: Boolean, val result: Result)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Result(
    val resource_id: String,
    val limit: Int,
    val total: Int,
    val fields: List<Field>,
    val records: List<Record>
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Field(val type: String, val id: String)

@Parcelize
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties("_id")
data class Record(
    val _id: Long?,
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