package com.mingmin.newtaipeipublicparking.map

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

interface GoogleMapProvider {
    fun loadMap(callback: (GoogleMap) -> Unit)
    fun loadMyLocation(callback: (LatLng) -> Unit)
}