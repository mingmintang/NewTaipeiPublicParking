package com.mingmin.newtaipeipublicparking.data

import com.google.android.gms.maps.model.LatLng
import com.mingmin.newtaipeipublicparking.data.remote.MapsApiLatLng
import com.mingmin.newtaipeipublicparking.data.remote.RouteService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test

class RouteRepositoryImplTest {
    private val key = "GoogleDirectionsKey"
    private val from = LatLng(24.9974558,121.4935593)
    private val to = LatLng(25.0017446,121.492276)
    private val route1 = Route(listOf(LatLng(24.9970895,121.4944662), LatLng(24.9990473,121.4911987)))
    private val route2 = Route(listOf(LatLng(24.9994203,121.492317), LatLng(24.9961572,121.4850823)))
    private val routes = listOf(route1, route2)
    private lateinit var repository: RouteRepository

    @MockK
    private lateinit var routeService: RouteService

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        repository = RouteRepositoryImpl(routeService)
    }

    @Test
    fun getRoutes() {
        every { routeService.getRoutes(any(), any(), any()) } returns Single.just(routes)
        val testObserver = TestObserver<List<Route>>()

        repository.getRoutes(from, to, key).subscribe(testObserver)

        verify { routeService.getRoutes(MapsApiLatLng(from), MapsApiLatLng(to), key) }
        testObserver.assertValue(routes)
    }
}