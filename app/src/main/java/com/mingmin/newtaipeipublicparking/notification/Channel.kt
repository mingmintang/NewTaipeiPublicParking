package com.mingmin.newtaipeipublicparking.notification

import android.app.NotificationChannel
import androidx.core.app.NotificationCompat

interface Channel {
    fun getNotificationBuilder(): NotificationCompat.Builder
    fun getNotificationChannel(): NotificationChannel
}