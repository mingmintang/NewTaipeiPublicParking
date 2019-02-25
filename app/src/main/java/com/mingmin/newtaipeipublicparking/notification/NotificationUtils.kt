package com.mingmin.newtaipeipublicparking.notification

import android.app.NotificationManager
import android.content.Context
import android.os.Build

class NotificationUtils {
    companion object {
        fun createNotificationChannels(context: Context, vararg channels: Channel) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val list = List(channels.size) { index ->
                    channels[index].getNotificationChannel()
                }
                val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannels(list)
            }
        }
    }
}