package com.mingmin.newtaipeipublicparking.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import com.mingmin.newtaipeipublicparking.R

class UpdateAllDataChannel(val context: Context) : Channel {
    private val contextTitle = context.getString(R.string.update_data_downloading)
    private val smallIcon = R.drawable.ic_parking_black_32dp
    private val priority = NotificationCompat.PRIORITY_LOW

    private val channelId = "update_all_data"
    private val channelName = context.getString(R.string.download_update_data)
    private val channelDescription = context.getString(R.string.download_update_data_description)
    @RequiresApi(Build.VERSION_CODES.N)
    private val channelImportance = NotificationManager.IMPORTANCE_LOW

    val notificationId = 1

    override fun getNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(smallIcon)
            .setContentTitle(contextTitle)
            .setPriority(priority)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getNotificationChannel(): NotificationChannel {
        return NotificationChannel(channelId, channelName, channelImportance).apply {
            description = channelDescription
        }
    }
}