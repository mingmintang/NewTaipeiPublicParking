package com.mingmin.newtaipeipublicparking.update_all_data

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import com.mingmin.newtaipeipublicparking.data.ParkingLot
import com.mingmin.newtaipeipublicparking.data.ParkingLotService
import com.mingmin.newtaipeipublicparking.db.ParkingLotDao
import com.mingmin.newtaipeipublicparking.notification.NotificationUtils
import com.mingmin.newtaipeipublicparking.notification.UpdateAllDataChannel
import com.mingmin.newtaipeipublicparking.parking_list.ParkingListActivity
import io.reactivex.Maybe
import io.reactivex.observers.DisposableMaybeObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class UpdateAllDataForegroundService : Service() {

    lateinit var updateAllDataChannel: UpdateAllDataChannel

    override fun onCreate() {
        super.onCreate()
        updateAllDataChannel = UpdateAllDataChannel(this)
        NotificationUtils.createNotificationChannels(this, updateAllDataChannel)
    }

    override fun onBind(intent: Intent): IBinder { return Binder() }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification(1)
        downloadAndSaveAllData()
        return START_NOT_STICKY
    }

    private fun downloadAndSaveAllData() {
        ParkingLotService.create().getAllParkingLots()
            .subscribeOn(Schedulers.io())
            .subscribe(object : DisposableSingleObserver<List<ParkingLot>>() {
                override fun onSuccess(parkingLots: List<ParkingLot>) {
                    showNotification(2)
                    saveAllData(parkingLots)
                }
                override fun onError(e: Throwable) {
                    sendBroadcast(false)
                }
            })
    }

    private fun saveAllData(parkingLots: List<ParkingLot>) {
        val parkingLotDao = ParkingLotDao.newInstance(this)
        Maybe.fromCallable { parkingLotDao.updateAll(parkingLots) }
            .subscribeOn(Schedulers.io())
            .subscribe(object : DisposableMaybeObserver<Unit>() {
                override fun onSuccess(result: Unit) {
                    sendBroadcast(true)
                }
                override fun onComplete() { sendBroadcast(false) }
                override fun onError(e: Throwable) { sendBroadcast(false) }
            })
    }

    private fun sendBroadcast(isSuccess: Boolean) {
        val intent = Intent()
        if (isSuccess) {
            intent.action = ParkingListActivity.ACTION_UPDATE_ALL_DATA_SUCCESS
        } else {
            intent.action = ParkingListActivity.ACTION_UPDATE_ALL_DATA_FAIL
        }
        LocalBroadcastManager.getInstance(this@UpdateAllDataForegroundService).sendBroadcast(intent)
        stopSelf()
    }

    private fun showNotification(progress: Int) {
        val builder = updateAllDataChannel.getNotificationBuilder()
        builder.setProgress(3, progress, false)
        startForeground(
            updateAllDataChannel.notificationId,
            builder.build()
        )
    }
}
