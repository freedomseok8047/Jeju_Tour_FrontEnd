package com.example.visit_jeju_app.tour

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.R
import com.example.visit_jeju_app.databinding.ActivityTourDetailBinding
import com.example.visit_jeju_app.tour.model.TourList
import com.example.visit_jeju_app.tour.model.TourModel
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.geometry.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TourDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mapView: com.naver.maps.map.MapView? = null
    lateinit var binding: ActivityTourDetailBinding
    override fun onCreate(savedInstanceState: Bundle?,) {
        binding= ActivityTourDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.name.text = intent.getStringExtra("itemsTitle")
        binding.addr1.text = intent.getStringExtra("itemsAddress")
        binding.addr2.text = intent.getStringExtra("itemsRoadAddress")
        binding.regionlable.text = intent.getStringExtra("itemsRegion2CdLabel")
        binding.info.text = intent.getStringExtra("itemsIntroduction")
        binding.tel.text = intent.getStringExtra("itemsPhoneNo")
        binding.convenience.text = intent.getStringExtra("itemsAllTag")

        val imageUrl = intent.getStringExtra("itemsRepPhotoPhotoidImgPath")
        Glide.with(this)
            .load(imageUrl)
            .override(450,350)
            .into(binding.itemImage)

        // 전화 버튼
        binding.callBtn.setOnClickListener {
            // 전화번호 가져오기
            val phoneNumber = intent.getStringExtra("itemsPhoneNo")
            // 전화 다이얼 화면으로 이동
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(intent)
        }


        //네이버 지도
        mapView = findViewById<View>(R.id.map_view) as com.naver.maps.map.MapView
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this@TourDetailActivity)

    }//onCreate

    override fun onMapReady(naverMap: NaverMap) {
        val networkService = (applicationContext as MyApplication).networkService
        val mapListCall = networkService.GetTourList()

        val uiSettings = Companion.naverMap?.uiSettings
        uiSettings?.isCompassEnabled = true
        uiSettings?.isLocationButtonEnabled = true

        mapListCall.enqueue(object : Callback<List<TourList>> {
            override fun onResponse(
                call: Call<List<TourList>>,
                response: Response<List<TourList>>

            ) {
                var TourList = response.body()

                // 마커 객체 생성
                val marker = Marker()

//                // o9 "itemsLatitude": 33.4840605,
//                @SerializedName("itemsLatitude")
//                var itemsLatitude : Double,
//
//                // 10 "itemsLongitude": 126.4160275,
//                @SerializedName("itemsLongitude")
//                var itemsLongitude : String,

                val lat: Double = intent.getDoubleExtra("itemsLatitude", Double.MAX_VALUE)
                val lnt: Double = intent.getDoubleExtra("itemsLongitude", Double.MAX_VALUE)


                // 가져온 위도, 경도 값으로 position 세팅
                marker.setPosition(LatLng(lat, lnt))
                marker.setMap(naverMap)

                val cameraPosition = CameraPosition( // 카메라 위치 변경
                    LatLng(lat,lnt),  // 위치 지정
                    10.0 // 줌 레벨
                )
                naverMap.cameraPosition = cameraPosition


            }

            override fun onFailure(call: Call<List<TourList>>, t: Throwable) {
                call.cancel()
            }


        })


    }

    companion object {
        private val naverMap: NaverMap? = null
    }

}