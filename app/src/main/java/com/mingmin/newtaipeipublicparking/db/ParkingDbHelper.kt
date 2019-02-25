package com.mingmin.newtaipeipublicparking.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ParkingDbHelper(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(ParkingLotDaoImpl.SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(ParkingLotDaoImpl.SQL_DROP_TABLE)
        onCreate(db)
    }

    companion object {
        private const val DB_NAME = "parking.db"
        private const val VERSION = 1

        private var instance: ParkingDbHelper? = null
        fun getInstance(context: Context?): ParkingDbHelper {
            if (instance == null) {
                instance = ParkingDbHelper(context, DB_NAME, null, VERSION)
            }
            return instance!!
        }
    }
}