package com.mingmin.newtaipeipublicparking.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.mingmin.newtaipeipublicparking.http.DownloadObject
import com.mingmin.newtaipeipublicparking.http.Record

class ParkingDAO(context: Context) {
    private val db = ParkingDbHelper.getWritableDatabase(context)

    fun count(): Int {
        var count = 0
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME", null)
        if (cursor.moveToNext()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    fun close() {
        db.close()
    }

    // return primary key
    fun insert(record: Record): Long {
        return db.insert(TABLE_NAME, null, recordToContentValues(record))
    }

    fun insertAll(downloadObject: DownloadObject) {
        downloadObject.result.records.forEach {
            insert(it)
        }
    }

    fun update(record: Record): Boolean {
        return db.update(
            TABLE_NAME,
            recordToContentValues(record),
            "$COLUMN_PRIMARY_KEY=${record._id}",
            null) > 0
    }

    fun updateAll(downloadObject: DownloadObject) {
        deleteAll()
        insertAll(downloadObject)
    }

    fun delete(key: Long): Boolean {
        return db.delete(TABLE_NAME, "$COLUMN_PRIMARY_KEY=$key", null) > 0
    }

    fun deleteAll() {
        return db.execSQL(SQL_CLEAR_TABLE)
    }

    fun queryAll(): ArrayList<Record>? {
        val records = ArrayList<Record>()
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            records.add(getRecord(cursor))
        }
        cursor.close()
        return if (records.isEmpty()) null else records
    }

    fun queryByAreaAndKeyword(area: String?, keyword: String?): ArrayList<Record>? {
        val records = ArrayList<Record>()
        var where = ""
        area?.let {
            where += "$COLUMN_AREA='$area'"
        }
        keyword?.let {
            if (where.length != 0) {
                where += " AND "
            }
            where += "$COLUMN_NAME LIKE '%$keyword%'"
        }
        val selection = if (where.isEmpty()) null else where
        val cursor = db.query(TABLE_NAME, null, selection, null, null, null, null)
        while (cursor.moveToNext()) {
            records.add(getRecord(cursor))
        }
        cursor.close()
        return if (records.isEmpty()) null else records
    }

    private fun getRecord(cursor: Cursor): Record {
        return Record(
            _id = cursor.getLong(0),
            ID = cursor.getInt(1),
            AREA = cursor.getString(2),
            NAME = cursor.getString(3),
            TYPE = cursor.getInt(4),
            SUMMARY = cursor.getString(5),
            ADDRESS = cursor.getString(6),
            TEL = cursor.getString(7),
            PAYEX = cursor.getString(8),
            SERVICETIME = cursor.getString(9),
            TW97X = cursor.getDouble(10),
            TW97Y = cursor.getDouble(11),
            TOTALCAR = cursor.getInt(12),
            TOTALMOTOR = cursor.getInt(13),
            TOTALBIKE = cursor.getInt(14)
        )
    }

    private fun recordToContentValues(record: Record): ContentValues {
        val cv = ContentValues()
        cv.put(COLUMN_ID, record.ID)
        cv.put(COLUMN_AREA, record.AREA)
        cv.put(COLUMN_NAME, record.NAME)
        cv.put(COLUMN_TYPE, record.TYPE)
        cv.put(COLUMN_SUMMARY, record.SUMMARY)
        cv.put(COLUMN_ADDRESS, record.ADDRESS)
        cv.put(COLUMN_TEL, record.TEL)
        cv.put(COLUMN_PAYEX, record.PAYEX)
        cv.put(COLUMN_SERVICETIME, record.SERVICETIME)
        cv.put(COLUMN_TW97X, record.TW97X)
        cv.put(COLUMN_TW97Y, record.TW97Y)
        cv.put(COLUMN_TOTALCAR, record.TOTALCAR)
        cv.put(COLUMN_TOTALMOTOR, record.TOTALMOTOR)
        cv.put(COLUMN_TOTALBIKE, record.TOTALBIKE)
        return cv
    }

    companion object {
        val TABLE_NAME = "parkings"
        val COLUMN_PRIMARY_KEY = "_id"
        val COLUMN_ID = "ID"
        val COLUMN_AREA = "AREA"
        val COLUMN_NAME = "NAME"
        val COLUMN_TYPE = "TYPE"
        val COLUMN_SUMMARY = "SUMMARY"
        val COLUMN_ADDRESS = "ADDRESS"
        val COLUMN_TEL = "TEL"
        val COLUMN_PAYEX = "PAYEX"
        val COLUMN_SERVICETIME = "SERVICETIME"
        val COLUMN_TW97X = "TW97X"
        val COLUMN_TW97Y = "TW97Y"
        val COLUMN_TOTALCAR = "TOTALCAR"
        val COLUMN_TOTALMOTOR = "TOTALMOTOR"
        val COLUMN_TOTALBIKE = "TOTALBIKE"

        val SQL_CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME (
            $COLUMN_PRIMARY_KEY INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_ID INTEGER NOT NULL,
            $COLUMN_AREA TEXT NOT NULL,
            $COLUMN_NAME TEXT NOT NULL,
            $COLUMN_TYPE INTEGER NOT NULL,
            $COLUMN_SUMMARY TEXT NOT NULL,
            $COLUMN_ADDRESS TEXT NOT NULL,
            $COLUMN_TEL TEXT NOT NULL,
            $COLUMN_PAYEX TEXT NOT NULL,
            $COLUMN_SERVICETIME TEXT NOT NULL,
            $COLUMN_TW97X REAL NOT NULL,
            $COLUMN_TW97Y REAL NOT NULL,
            $COLUMN_TOTALCAR INTEGER NOT NULL,
            $COLUMN_TOTALMOTOR INTEGER NOT NULL,
            $COLUMN_TOTALBIKE INTEGER NOT NULL)
        """.trimIndent()

        val SQL_DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"

        val SQL_CLEAR_TABLE = "DELETE FROM $TABLE_NAME"
    }
}