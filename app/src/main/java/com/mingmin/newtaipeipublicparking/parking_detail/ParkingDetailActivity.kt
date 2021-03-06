package com.mingmin.newtaipeipublicparking.parking_detail

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import com.google.android.material.snackbar.Snackbar
import androidx.test.espresso.IdlingResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.mingmin.newtaipeipublicparking.Injection
import com.mingmin.newtaipeipublicparking.R
import com.mingmin.newtaipeipublicparking.data.ParkingLot
import com.mingmin.newtaipeipublicparking.data.Route
import com.mingmin.newtaipeipublicparking.map.AndroidGoogleMapProvider
import com.mingmin.newtaipeipublicparking.util.Converts
import com.mingmin.newtaipeipublicparking.util.EspressoIdlingResource
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_parking_detail.*

class ParkingDetailActivity : AppCompatActivity(), ParkingDetailContract.View {
    private val disposables = CompositeDisposable()
    private lateinit var parkingLot: ParkingLot
    private lateinit var map: GoogleMap
    private lateinit var parkingLotLocation: LatLng
    private lateinit var presenter: ParkingDetailContract.ActionsListener
    private var myLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_detail)

        parkingLot = intent.getParcelableExtra(KEY_PARKING_LOT)
        parkingLotLocation = Converts.twd97ToLatLng(parkingLot.TW97X, parkingLot.TW97Y)
        setupViews()
        val googleMapProvider = AndroidGoogleMapProvider(this,
            supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment)
        presenter = ParkingDetailPresenter(
            googleMapProvider,
            disposables,
            Injection.provideRouteRepository(),
            Injection.provideSchedulerProvider(),
            this
        )
        presenter.loadMap()
    }

    private fun setupViews() {
        toolbar.title = parkingLot.NAME
        parking_name.text = parkingLot.NAME
        area.text = parkingLot.AREA
        servicetime.text = parkingLot.SERVICETIME
        address.text = parkingLot.ADDRESS

        toolbar.setNavigationOnClickListener {
            finishAfterTransition()
        }
    }

    override fun showMap(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.setMinZoomPreference(7.0f)
        map.addMarker(MarkerOptions().position(parkingLotLocation).title(parkingLot.NAME))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(parkingLotLocation, 18.0f))

        setupMyLocation()
    }

    override fun showMyLocation(myLocation: LatLng) {
        this.myLocation = myLocation
    }

    override fun showRoutes(myLocation: LatLng, parkingLotLocation: LatLng, routes: List<Route>) {
        val colorArray = arrayOf(
            Color.parseColor("#77F20B0B"),
            Color.parseColor("#77680DE5"),
            Color.parseColor("#777E4503"))

        val maxRouteCount = 3
        val builder = LatLngBounds.builder()
        for ((index, route) in routes.withIndex()) {
            if (index < maxRouteCount) {
                drawRoute(myLocation, parkingLotLocation, route.points, colorArray[index], builder)
            }
        }
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100))
    }

    override fun showRoutesButton() {
        routes.visibility = View.VISIBLE
        routes_shadow.visibility = View.VISIBLE
    }

    override fun showMapLoading(isEnabled: Boolean) {
        if (isEnabled) {
            map_loading.visibility = View.VISIBLE
            routes.isEnabled = false
        } else {
            map_loading.visibility = View.GONE
            routes.isEnabled = true
        }
    }

    override fun showLoadMapFail() {
        Snackbar.make(map_fragment.view!!, getString(R.string.load_map_fail), Snackbar.LENGTH_SHORT).show()
    }

    override fun showLoadRoutesFail() {
        Snackbar.make(map_fragment.view!!, getString(R.string.load_routes_fail), Snackbar.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (permissions.size == 1 &&
            permissions[0] == ACCESS_FINE_LOCATION &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupMyLocation()
        }
    }

    private fun setupMyLocation() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), 1)
            return
        }
        map.isMyLocationEnabled = true
        presenter.loadMyLocation()
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
        myLocation?.let {
            presenter.loadRoutes(it, parkingLotLocation, resources.getString(R.string.google_directions_key))
        }
    }

    @VisibleForTesting
    fun getCountingIdlingResource(): IdlingResource {
        return EspressoIdlingResource.getIdlingResource()
    }

    companion object {
        const val KEY_PARKING_LOT = "ParkingLot"
    }
}
