package com.mingmin.newtaipeipublicparking.update_all_data

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mingmin.newtaipeipublicparking.Injection
import com.mingmin.newtaipeipublicparking.data.ParkingLot
import com.mingmin.newtaipeipublicparking.data.ParkingLotRepository
import com.mingmin.newtaipeipublicparking.notification.NotificationUtils
import com.mingmin.newtaipeipublicparking.notification.UpdateAllDataChannel
import com.mingmin.newtaipeipublicparking.parking_list.ParkingListActivity
import com.mingmin.newtaipeipublicparking.util.EspressoIdlingResource
import com.mingmin.newtaipeipublicparking.util.schedulers.AndroidSchedulerProvider
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

class UpdateAllDataForegroundService : Service() {

    private lateinit var updateAllDataChannel: UpdateAllDataChannel
    private lateinit var parkingLotRepository: ParkingLotRepository
    private val schedulerProvider = AndroidSchedulerProvider()

    override fun onCreate() {
        super.onCreate()
        updateAllDataChannel = UpdateAllDataChannel(this)
        parkingLotRepository = Injection.provideParkingLotRepository(this)
        NotificationUtils.createNotificationChannels(this, updateAllDataChannel)
    }

    override fun onBind(intent: Intent): IBinder { return Binder() }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification(1)
        downloadAndSaveAllData()
        return START_NOT_STICKY
    }

    private fun downloadAndSaveAllData() {
        EspressoIdlingResource.increment()
        parkingLotRepository.getAndSaveRemoteParkingLots()
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .doFinally { EspressoIdlingResource.decrement() }
            .subscribe(object : SingleObserver<List<ParkingLot>> {
                override fun onSubscribe(d: Disposable) {}
                override fun onSuccess(parkingLots: List<ParkingLot>) {
                    showNotification(2)
                    sendBroadcast(true)
                }
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
