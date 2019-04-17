package com.mingmin.newtaipeipublicparking.map

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class AndroidGoogleMapProvider(val context: Context, val mapFragment: SupportMapFragment) : GoogleMapProvider {
    override fun loadMap(callback: (GoogleMap) -> Unit) {
        mapFragment.getMapAsync { googleMap ->
            callback(googleMap)
        }
    }

    @SuppressLint("MissingPermission")
    override fun loadMyLocation(callback: (LatLng) -> Unit) {
        LocationServices.getFusedLocationProviderClient(context)
            .lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val location = task.result
                location?.let { callback(LatLng(it.latitude, it.longitude)) }
            }
        }
    }
}