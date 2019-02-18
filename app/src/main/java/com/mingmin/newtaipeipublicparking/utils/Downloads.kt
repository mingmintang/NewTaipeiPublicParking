package com.mingmin.newtaipeipublicparking.utils

import okhttp3.*
import java.io.IOException

class Downloads {
    companion object {
        fun okHttpDownload(url: String, callback: (String?) -> Unit) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()
            val call = client.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(null)
                }
                override fun onResponse(call: Call, response: Response) {
                    callback(response.body()?.string())
                }
            })
        }
    }
}