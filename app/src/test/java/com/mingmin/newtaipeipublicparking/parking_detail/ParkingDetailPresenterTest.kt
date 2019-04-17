package com.mingmin.newtaipeipublicparking.parking_detail

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.mingmin.newtaipeipublicparking.data.Route
import com.mingmin.newtaipeipublicparking.data.RouteRepository
import com.mingmin.newtaipeipublicparking.map.GoogleMapProvider
import com.mingmin.newtaipeipublicparking.util.schedulers.ImmediateSchedulerProvider
import com.mingmin.newtaipeipublicparking.util.schedulers.SchedulerProvider
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verifyOrder
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Test

class ParkingDetailPresenterTest {
    private val myLocation = LatLng(24.0109, 120.4644)
    private val parkingLotLocation = LatLng(25.0109, 121.4644)
    private val routes = listOf<Route>(Route(listOf(LatLng(24.0109, 120.4644))))
    private val googleDirectionsKey = ""
    private lateinit var schedulerProvider: SchedulerProvider
    private lateinit var disposables: CompositeDisposable
    private lateinit var presenter: ParkingDetailPresenter

    @MockK
    private lateinit var googleMap: GoogleMap
    @MockK
    private lateinit var googleMapProvider: GoogleMapProvider
    @MockK
    private lateinit var routeRepository: RouteRepository
    @MockK
    private lateinit var parkingDetailView: ParkingDetailContract.View

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        disposables = CompositeDisposable()
        schedulerProvider = ImmediateSchedulerProvider()
        presenter = ParkingDetailPresenter(googleMapProvider,
            disposables, routeRepository, schedulerProvider, parkingDetailView)
    }

    @Test
    fun loadMap_googleMapExisted_showGoogleMap() {
        every { googleMapProvider.loadMap(any()) } answers {
            arg<(GoogleMap) -> Unit>(0).invoke(googleMap)
        }

        presenter.loadMap()

        verifyOrder {
            parkingDetailView.showMapLoading(true)
            parkingDetailView.showMap(googleMap)
            parkingDetailView.showMapLoading(false)
        }
    }

    @Test
    fun loadMyLocation_myLocationExisted_showMyLocation() {
        every { googleMapProvider.loadMyLocation(any()) } answers {
            arg<(LatLng) -> Unit>(0).invoke(myLocation)
        }

        presenter.loadMyLocation()

        verifyOrder {
            parkingDetailView.showMyLocation(myLocation)
            parkingDetailView.showRoutesButton()
        }
    }

    @Test
    fun loadRoutes_loadSuccess_showRoutes() {
        every {
            routeRepository.getRoutes(any(), any(), any())
        } returns Single.just(routes)

        presenter.loadRoutes(myLocation, parkingLotLocation, googleDirectionsKey)

        verifyOrder {
            parkingDetailView.showMapLoading(true)
            parkingDetailView.showRoutes(myLocation, parkingLotLocation, routes)
            parkingDetailView.showMapLoading(false)
        }
    }

    @Test
    fun loadRoutes_loadFail_showLoadRoutesFail() {
        every {
            routeRepository.getRoutes(any(), any(), any())
        } returns Single.error(Throwable())

        presenter.loadRoutes(myLocation, parkingLotLocation, googleDirectionsKey)

        verifyOrder {
            parkingDetailView.showMapLoading(true)
            parkingDetailView.showLoadRoutesFail()
            parkingDetailView.showMapLoading(false)
        }
    }
}