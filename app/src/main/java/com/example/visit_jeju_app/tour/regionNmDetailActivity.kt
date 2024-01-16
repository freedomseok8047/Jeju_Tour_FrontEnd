package com.example.visit_jeju_app.tour

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.visit_jeju_app.MainActivity
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.R
import com.example.visit_jeju_app.accommodation.AccomActivity
import com.example.visit_jeju_app.chat.ChatActivity
import com.example.visit_jeju_app.chat.ChatMainActivity
import com.example.visit_jeju_app.community.activity.CommReadActivity
import com.example.visit_jeju_app.databinding.ActivityRegionNmDetailBinding
import com.example.visit_jeju_app.festival.FesActivity
import com.example.visit_jeju_app.gpt.GptActivity
import com.example.visit_jeju_app.login.AuthActivity
import com.example.visit_jeju_app.restaurant.ResActivity
import com.example.visit_jeju_app.shopping.ShopActivity
import com.example.visit_jeju_app.tour.model.TourList
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import retrofit2.Call
import retrofit2.Response

class regionNmDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding: ActivityRegionNmDetailBinding
    private var mapView: MapView? = null

    //액션버튼 토글(공통 레이아웃 코드)
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegionNmDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 공통 레이아웃 코드 시작 --------------------------------------------------------------
        setSupportActionBar(binding.toolbar)

        //(공통 레이아웃 코드)
        // SharedPreferences에서 이메일 주소 불러오기
        val sharedPref = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val userEmail = sharedPref.getString("USER_EMAIL", "No Email") // 기본값 "No Email"

        // 네비게이션 드로어 헤더의 이메일 TextView 업데이트
        val headerView = binding.mainDrawerView.getHeaderView(0)
        val headerUserEmail = headerView.findViewById<TextView>(R.id.headerUserEmail)
        headerUserEmail.text = userEmail

        val headerLogoutBtn = headerView.findViewById<Button>(R.id.headerLogoutBtn)

        headerLogoutBtn.setOnClickListener {
            // 로그아웃 로직
            MyApplication.auth.signOut()
            MyApplication.email = null
            // 로그아웃 후 처리 (예: 로그인 화면으로 이동)
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }


        setSupportActionBar(binding.toolbar)


        //드로워화면 액션버튼 클릭 시 드로워 화면 나오게 하기(공통 레이아웃 코드)
        toggle =
            ActionBarDrawerToggle(this@regionNmDetailActivity, binding.drawerLayout,R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)

        //화면 적용하기(공통 레이아웃 코드)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //버튼 클릭스 동기화 : 드로워 열어주기(공통 레이아웃 코드)
        toggle.syncState()

        // NavigationView 메뉴 아이템 클릭 리스너 설정
        binding.mainDrawerView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.accommodation -> {
                    val intent = Intent(this, AccomActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }

                R.id.restaurant -> {
                    val intent = Intent(this, ResActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }

                R.id.tour -> {
                    val intent = Intent(this, TourActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }

                R.id.festival -> {
                    val intent = Intent(this, FesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }

                R.id.shopping -> {
                    val intent = Intent(this, ShopActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }

                R.id.community -> {
                    val intent = Intent(this, CommReadActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }

                R.id.chatting -> {
                    val intent = Intent(this, ChatMainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        // Bottom Navigation link
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.home -> {
                    // 홈 아이템 클릭 처리
                    val intent = Intent(this@regionNmDetailActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP // 액티비티 새로 생성 방지
                    startActivity(intent)
                    true
                }
                R.id.chat -> {
                    val intent = Intent(this@regionNmDetailActivity, GptActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP // 액티비티 새로 생성 방지
                    startActivity(intent)
                    true
                }
                R.id.youtube -> {
                    val webpageUrl = "https://www.youtube.com/c/visitjeju" // 웹 페이지 링크
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webpageUrl))
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP // 액티비티 새로 생성 방지
                    startActivity(intent)
                    true
                }
                R.id.instagram -> {
                    val webpageUrl = "https://www.instagram.com/visitjeju.kr" // 웹 페이지 링크
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webpageUrl))
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP // 액티비티 새로 생성 방지
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // 공통 레이아웃 코드 끝 --------------------------------------------------------------


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
        mapView!!.getMapAsync(this@regionNmDetailActivity)

    }//onCreate

    // 함수 구현 ---------------------------------------------------------------------------

    // Bottom Navigation link(공통 레이아웃 코드)
    private fun openWebPage(url: String) {
        val webpage = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // 툴바의 검색 뷰(공통 레이아웃)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu,menu)

        // 검색 뷰에, 이벤트 추가하기.
        val menuItem = menu?.findItem(R.id.menu_toolbar_search)
        // menuItem 의 형을 SearchView 타입으로 변환, 형변환
        val searchView = menuItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                //검색어가 변경시 마다, 실행될 로직을 추가.
                Log.d("kmk","텍스트 변경시 마다 호출 : ${newText} ")
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                // 검색어가 제출이 되었을 경우, 연결할 로직.
                // 사용자 디비, 검색을하고, 그 결과 뷰를 출력하는 형태.
                Toast.makeText(this@regionNmDetailActivity,"검색어가 전송됨 : ${query}", Toast.LENGTH_SHORT).show()
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onMapReady(naverMap: NaverMap) {

//        val networkService = (applicationContext as MyApplication).networkService
//        val tourId : Long = intent.getLongExtra("tourId",Long.MIN_VALUE)
//        Log.d("ljs", "intent로 받아온 tourId 값 확인 : ${tourId}")
//        val mapListCall = networkService.getTourDtl(tourId)
//
//        mapListCall?.enqueue(object : retrofit2.Callback<List<TourList>> {
//            override fun onResponse(
//                call: Call<List<TourList>>,
//                response: Response<List<TourList>>
//            ) {
//                var TourList = response.body()

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


//            }
//
//            override fun onFailure(call: Call<List<TourList>>, t: Throwable) {
//                call.cancel()
//            }
//
//        })

    }

}
