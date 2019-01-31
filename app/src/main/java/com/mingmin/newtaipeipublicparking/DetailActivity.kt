package com.mingmin.newtaipeipublicparking

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.mingmin.newtaipeipublicparking.http.Downloads
import com.mingmin.newtaipeipublicparking.http.Record
import com.mingmin.newtaipeipublicparking.utils.Converts
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var record: Record
    private lateinit var map: GoogleMap
    private lateinit var parkingLocation: LatLng
    private var myLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        record = intent.getParcelableExtra("record")
        setupViews()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (permissions.size == 1 &&
            permissions[0] == ACCESS_FINE_LOCATION &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupMyLocation()
        }
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

        showLoading(true)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.detail_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap?.let {
            map = it
            map.uiSettings.isZoomControlsEnabled = true
            map.setMinZoomPreference(7.0f)
            parkingLocation = Converts.twd97ToLatLong(record.TW97X, record.TW97Y)
            map.addMarker(MarkerOptions().position(parkingLocation).title(record.NAME))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(parkingLocation, 18.0f))

            setupMyLocation()
        }
        showLoading(false)
    }

    private fun setupMyLocation() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
            != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), 1)
            return
        }

        map.isMyLocationEnabled = true
        LocationServices.getFusedLocationProviderClient(this)
            .lastLocation.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val location = task.result
                location?.let {
                    myLocation = LatLng(it.latitude, it.longitude)
                    showRoutesButton()
                }
            }
        }
    }

    private fun drawRoutes(from: LatLng, to: LatLng) {
        showLoading(true)
        val colorArray = arrayOf(
            Color.parseColor("#77F20B0B"),
            Color.parseColor("#77680DE5"),
            Color.parseColor("#777E4503"))

        Downloads.getRoutesFromDirectionsAPI(from, to, resources.getString(R.string.google_directions_key))
            .addOnCompleteListener { showLoading(false) }
            .addOnFailureListener { e -> println(e) }
            .addOnSuccessListener { routes ->
                val maxRouteCount = 3
                val builder = LatLngBounds.builder()
                for ((index, points) in routes.withIndex()) {
                    if (index < maxRouteCount) {
                        drawRoute(from, to, points, colorArray[index], builder)
                    }
                }
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100))
            }
    }

    private fun drawRoute(from: LatLng, to: LatLng, points: List<LatLng>,
                          color: Int, builder: LatLngBounds.Builder) {
        val options = PolylineOptions()
        options.color(color)
        options.width(20f)
        options.add(from)
        builder.include(from)
        points.forEach { point ->
            options.add(point)
            builder.include(point)
        }
        options.add(to)
        builder.include(to)
        map.addPolyline(options)
    }

    fun onRoutesClick(view: View) {
        myLocation?.let { drawRoutes(it, parkingLocation) }
    }

    private fun showRoutesButton() {
        detail_routes.visibility = View.VISIBLE
        detail_routes_shadow.visibility = View.VISIBLE
    }

    private fun showLoading(isEnabled: Boolean) {
        if (isEnabled) {
            detail_map_loading.visibility = View.VISIBLE
            detail_routes.isEnabled = false
        } else {
            detail_map_loading.visibility = View.GONE
            detail_routes.isEnabled = true
        }
    }
}
