package com.example.visit_jeju_app.festival

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.R
import com.example.visit_jeju_app.databinding.ActivityRegionNmDetailFesBinding
import com.example.visit_jeju_app.festival.model.FesList
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import retrofit2.Call
import retrofit2.Response

class regionNmDetailFesActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding: ActivityRegionNmDetailFesBinding
    private var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegionNmDetailFesBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.facltNm.text = intent.getStringExtra("itemsTitle")
        binding.tel.text = intent.getStringExtra("itemsPhoneNo")
        binding.lable.text = intent.getStringExtra("itemsContentsCdLabel")
        binding.lineIntro.text = intent.getStringExtra("itemsRegion2CdLabel")
        binding.intro.text = intent.getStringExtra("itemsIntroduction")
        binding.sbrsCl.text = intent.getStringExtra("itemsAllTag")
        binding.addr1.text = intent.getStringExtra("itemsRoadAddress")
        binding.region.text = intent.getStringExtra("itemsRegion2CdLabel")

        val imgUrl: String? = intent.getStringExtra("itemsRepPhotoPhotoidImgPath")

        var itemsPhoneNo : String? = intent.getStringExtra("itemsPhoneNo")

        // 전화 버튼
        binding.callBtn.setOnClickListener {
            var phoneNumber = intent.getStringExtra("itemsPhoneNo")
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
            startActivity(intent)
        }

        Glide.with(this)
            .asBitmap()
            .load(imgUrl)
            .into(object : CustomTarget<Bitmap>(200, 200) {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    binding.avatarView.setImageBitmap(resource)
//                    Log.d("lsy", "width : ${resource.width}, height: ${resource.height}")
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })

        mapView = findViewById<View>(R.id.map_view) as MapView
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this@regionNmDetailFesActivity)

    }//onCreate

    override fun onMapReady(naverMap: NaverMap) {

        val networkService = (applicationContext as MyApplication).networkService
        var jejuRegionCode = intent.getIntExtra("itemsRegion2CdValue",11)
        val mapListCall = jejuRegionCode?.let { networkService.getFesList(it) }


        mapListCall?.enqueue(object : retrofit2.Callback<List<FesList>> {
            override fun onResponse(
                call: Call<List<FesList>>,
                response: Response<List<FesList>>
            ) {
                var FesList = response.body()

                // 마커 객체 생성
                val marker = Marker()

                // DB의 첫번째 행 식당의 위도, 경도 값 가져와 변수에 넣기
                val lat: Double = intent.getDoubleExtra("itemsLatitude", Double.MAX_VALUE)
                val lnt: Double = intent.getDoubleExtra("itemsLongitude", Double.MAX_VALUE)

                // 가져온 위도, 경도 값으로 position 세팅
                marker.setPosition(LatLng(lat, lnt))
                marker.setMap(naverMap)

                val cameraPosition = CameraPosition( // 카메라 위치 변경
                    LatLng(lat, lnt),  // 위치 지정
                    10.0 // 줌 레벨
                )
                naverMap.cameraPosition = cameraPosition


            }

            override fun onFailure(call: Call<List<FesList>>, t: Throwable) {
                call.cancel()
            }

        })

    }

}
