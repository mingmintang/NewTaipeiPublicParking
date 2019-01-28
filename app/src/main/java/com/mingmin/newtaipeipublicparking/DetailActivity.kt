package com.mingmin.newtaipeipublicparking

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.mingmin.newtaipeipublicparking.http.Record
import com.mingmin.newtaipeipublicparking.utils.Converts
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var record: Record

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        record = intent.getParcelableExtra("record")
        setupViews()
    }

    private fun setupViews() {
        detail_toolbar.title = record.NAME
        detail_parking_name.text = record.NAME
        detail_area.text = record.AREA
        detail_servicetime.text = record.SERVICETIME
        detail_address.text = record.ADDRESS

        detail_toolbar.setNavigationOnClickListener {
            finishAfterTransition()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.detail_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.let { map ->
            map.uiSettings.isZoomControlsEnabled = true
            map.setMinZoomPreference(12.0f)
            val latlng = Converts.twd97ToLatLong(record.TW97X, record.TW97Y)
            map.addMarker(MarkerOptions().position(latlng).title(record.NAME))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18.0f))
        }
    }
}
