package com.mingmin.newtaipeipublicparking.notification

import android.app.NotificationChannel
import android.support.v4.app.NotificationCompat

interface Channel {
    fun getNotificationBuilder(): NotificationCompat.Builder
    fun getNotificationChannel(): NotificationChannel
}