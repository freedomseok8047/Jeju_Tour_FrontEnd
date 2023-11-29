package com.example.visit_jeju_app.tour

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.camp.campingapp.model.NaverReverseGeocodeResponse
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.databinding.ActivityTourBinding
import com.example.visit_jeju_app.retrofit.NaverNetworkService
import com.example.visit_jeju_app.tour.adapter.TourAdapter
import com.example.visit_jeju_app.tour.model.TourList
import com.example.visit_jeju_app.tour.model.TourModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class TourActivity : AppCompatActivity() {

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null //현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    private lateinit var handler: Handler
    private var lastUpdateTimestamp = 0L
    private val updateDelayMillis = 10000
    //리사이클러 뷰 업데이트 딜레이 업데이트 주기 생성

    lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는
    private val REQUEST_PERMISSION_LOCATION = 10

//    private var mapX : String = ""
//    private var mapY : String= ""
//    private var coords: String = ""


    lateinit var binding: ActivityTourBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTourBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler = Handler(Looper.getMainLooper())

        mLocationRequest = LocationRequest.create().apply {

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 30000
            //초기화 시간 30초

        }

        if (checkPermissionForLocation(this)) {
            startLocationUpdates()

        }
    }//oncreate


    private fun startLocationUpdates() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        mFusedLocationProviderClient!!.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { onLocationChanged(it) }
        }
    }
    private fun onLocationChanged(location: Location) {
        mLastLocation = location
        val coords = "${mLastLocation.longitude},${mLastLocation.latitude}"
        getTourListWithinRadius(coords)
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        // 반경 필터링
        val R = 6371.0 // 지구의 반지름 (단위: km)

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return R * c
    }

    private fun getTourListWithinRadius(coords: String) {

        val networkService = (applicationContext as MyApplication).networkService
        val tourListCall = networkService.GetTourList()

        tourListCall.enqueue(object : Callback<List<TourList>> {
            override fun onResponse(
                call: Call<List<TourList>>,
                response: Response<List<TourList>>

            ) {
                val tourList = response.body()

                Log.d("ljs","tourModel 값 : ${tourList}")

                val centerLatitude = mLastLocation.latitude
                val centerLongitude = mLastLocation.longitude
                val radius = 5.0 // 5km 반경


                val touristSpotsWithinRadius = tourList?.mapNotNull { spot ->
                    val distance = haversineDistance(
                        centerLatitude, centerLongitude,
                        spot.itemsLatitude, spot.itemsLongitude
                    )
                    if (distance <= radius) {
                        spot // 관광지 데이터 객체 자체를 반환
                    } else {
                        null
                    }
                }

                val currentTime = System.currentTimeMillis()

                // 일정 시간이 지나지 않았으면 업데이트를 건너뜁니다.
                if (currentTime - lastUpdateTimestamp < updateDelayMillis) {
                    return
                }

                lastUpdateTimestamp = currentTime

                val layoutManager = LinearLayoutManager(this@TourActivity)

                binding.recyclerView.layoutManager = layoutManager

                binding.recyclerView.adapter =
                    TourAdapter(this@TourActivity,touristSpotsWithinRadius)


                binding.recyclerView.addItemDecoration(
                    DividerItemDecoration(this@TourActivity, LinearLayoutManager.VERTICAL)
                )

            }


            override fun onFailure(call: Call<List<TourList>>, t: Throwable) {
                Log.d("lsy", "fail")
                call.cancel()
            }
        })
    }

    private fun checkPermissionForLocation(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_PERMISSION_LOCATION
                )
                false
            }
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                Toast.makeText(this, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // 핸들러 메시지 제거
    }
}
