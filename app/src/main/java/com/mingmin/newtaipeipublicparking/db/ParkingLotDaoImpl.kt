package com.mingmin.newtaipeipublicparking.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.mingmin.newtaipeipublicparking.data.ParkingLot

class ParkingLotDaoImpl(context: Context) : ParkingLotDao {
    private val db = ParkingDbHelper.getWritableDatabase(context)

    override fun count(): Int {
        var count = 0
        val cursor = db.rawQuery(SQL_GET_COUNT, null)
        if (cursor.moveToNext()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    override fun close() {
        db.close()
    }

    override fun insert(parkinglot: ParkingLot) {
        db.insert(TABLE_NAME, null, parkingLotToContentValues(parkinglot))
    }

    override fun insertAll(parkingLots: Collection<ParkingLot>) {
        parkingLots.forEach { insert(it) }
    }

    override fun update(parkinglot: ParkingLot) {
        db.update(
            TABLE_NAME,
            parkingLotToContentValues(parkinglot),
            "${COLUMN_PRIMARY_KEY}=${parkinglot._id}",
            null)
    }

    override fun updateAll(parkingLots: Collection<ParkingLot>) {
        deleteAll()
        insertAll(parkingLots)
    }

    override fun deleteAll() {
        db.execSQL(SQL_CLEAR_TABLE)
    }

    override fun queryAll(): ArrayList<ParkingLot>? {
        val parkingLots = ArrayList<ParkingLot>()
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            parkingLots.add(getParkingLot(cursor))
        }
        cursor.close()
        return if (parkingLots.isEmpty()) null else parkingLots
    }

    override fun queryByAreaAndKeyword(area: String?, keyword: String?): ArrayList<ParkingLot>? {
        val parkingLots = ArrayList<ParkingLot>()
        var where = ""
        area?.let {
            where += "$COLUMN_AREA='$it'"
        }
        keyword?.let {
            if (where.isNotEmpty()) {
                where += " AND "
            }
            where += "$COLUMN_NAME LIKE '%$it%'"
        }
        val selection = if (where.isEmpty()) null else where
        val cursor = db.query(TABLE_NAME, null, selection, null, null, null, null)
        while(cursor.moveToNext()) {
            parkingLots.add(getParkingLot(cursor))
        }
        cursor.close()
        return if (parkingLots.isEmpty()) null else parkingLots
    }

    private fun getParkingLot(cursor: Cursor): ParkingLot {
        return ParkingLot(
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

    private fun parkingLotToContentValues(parkingLot: ParkingLot): ContentValues {
        val cv = ContentValues()
        cv.put(COLUMN_ID, parkingLot.ID)
        cv.put(COLUMN_AREA, parkingLot.AREA)
        cv.put(COLUMN_NAME, parkingLot.NAME)
        cv.put(COLUMN_TYPE, parkingLot.TYPE)
        cv.put(COLUMN_SUMMARY, parkingLot.SUMMARY)
        cv.put(COLUMN_ADDRESS, parkingLot.ADDRESS)
        cv.put(COLUMN_TEL, parkingLot.TEL)
        cv.put(COLUMN_PAYEX, parkingLot.PAYEX)
        cv.put(COLUMN_SERVICETIME, parkingLot.SERVICETIME)
        cv.put(COLUMN_TW97X, parkingLot.TW97X)
        cv.put(COLUMN_TW97Y, parkingLot.TW97Y)
        cv.put(COLUMN_TOTALCAR, parkingLot.TOTALCAR)
        cv.put(COLUMN_TOTALMOTOR, parkingLot.TOTALMOTOR)
        cv.put(COLUMN_TOTALBIKE, parkingLot.TOTALBIKE)
        return cv
    }

    companion object {
        const val TABLE_NAME = "ParkingLots"
        const val COLUMN_PRIMARY_KEY = "_id"
        const val COLUMN_ID = "ID"
        const val COLUMN_AREA = "AREA"
        const val COLUMN_NAME = "NAME"
        const val COLUMN_TYPE = "TYPE"
        const val COLUMN_SUMMARY = "SUMMARY"
        const val COLUMN_ADDRESS = "ADDRESS"
        const val COLUMN_TEL = "TEL"
        const val COLUMN_PAYEX = "PAYEX"
        const val COLUMN_SERVICETIME = "SERVICETIME"
        const val COLUMN_TW97X = "TW97X"
        const val COLUMN_TW97Y = "TW97Y"
        const val COLUMN_TOTALCAR = "TOTALCAR"
        const val COLUMN_TOTALMOTOR = "TOTALMOTOR"
        const val COLUMN_TOTALBIKE = "TOTALBIKE"

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

        const val SQL_GET_COUNT = "SELECT COUNT(*) FROM $TABLE_NAME"
        const val SQL_DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
        const val SQL_CLEAR_TABLE = "DELETE FROM $TABLE_NAME"
    }
}