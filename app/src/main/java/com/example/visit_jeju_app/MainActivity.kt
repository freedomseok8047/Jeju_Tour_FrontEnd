package com.example.visit_jeju_app


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.example.visit_jeju_app.chat.ChatMainActivity
import com.example.visit_jeju_app.community.activity.CommReadActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.visit_jeju_app.chat.ChatActivity
import com.example.visit_jeju_app.databinding.ActivityMainBinding
import com.example.visit_jeju_app.main.adapter.ImageSliderAdapter
import com.example.visit_jeju_app.main.adapter.RecyclerView
import com.example.visit_jeju_app.retrofit.NetworkServiceRegionNm
import com.example.visit_jeju_app.tour.model.TourList
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    // 위치 정보 받아오기 위한 변수 선언 -------------------------------------
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    // -----------------------------------------------------------

    lateinit var binding: ActivityMainBinding

    //액션버튼 토글
    lateinit var toggle: ActionBarDrawerToggle

    //lateinit var viewPager_aespa: ViewPager2
    lateinit var viewPager_mainVisual: ViewPager2



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 위치 받아오기 위해 추가 ---------------------------------------------------------
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()
        createLocationCallback()
        //---------------------------------------------------------

        setSupportActionBar(binding.toolbar)

        //드로워화면 액션버튼 클릭 시 드로워 화면 나오게 하기
        toggle =
            ActionBarDrawerToggle(this@MainActivity, binding.drawerLayout,R.string.open, R.string.close)

        binding.drawerLayout.addDrawerListener(toggle)
        //화면 적용하기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //버튼 클릭스 동기화 : 드로워 열어주기
        toggle.syncState()

        // NavigationView 메뉴 아이템 클릭 리스너 설정
        binding.mainDrawerView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.community -> {
                    // '커뮤니티' 메뉴 아이템 클릭 시 CommReadActivity로 이동
                    startActivity(Intent(this, CommReadActivity::class.java))
                    true
                }
                // 다른 메뉴 아이템에 대한 처리 추가

                else -> false
            }
        }

        // 리사이클러 뷰 붙이기
        val datasHotel = mutableListOf<String>()
        for(i in 1..5) {
            datasHotel.add("제주 숙박 : $i")
        }
        val datasRestaurant = mutableListOf<String>()
        for(i in 1..5) {
            datasRestaurant.add("제주 맛집 : $i")
        }
        val datasTour = mutableListOf<String>()
        for(i in 1..5) {
            datasTour.add("제주 투어 : $i")
        }
        val datasFestival = mutableListOf<String>()
        for(i in 1..5) {
            datasFestival.add("제주 축제 : $i")
        }
        val datasShopping = mutableListOf<String>()
        for(i in 1..5) {
            datasShopping.add("제주 쇼핑 : $i")
        }

        // 제주 숙박
//        val horizontalLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        //val linearLayoutManager = LinearLayoutManager(this)
//
//        // 리사이클러 뷰 속성 옵션에 출력 옵션 붙이기
//        binding.viewRecyclerHotel.layoutManager = horizontalLayoutManager
//        // 리사이클러뷰 속성 옵션에 데이터를 붙이기 , 어댑터 를 연결한다.
//        val customAdapter1 = RecyclerView(datasHotel)
//        binding.viewRecyclerHotel.adapter = customAdapter1

//       // 제주 맛집
//        val restaurantLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        binding.viewRecyclerRestaurant.layoutManager = restaurantLayoutManager
//        val customAdapter2 = RecyclerView(datasRestaurant)
//        binding.viewRecyclerRestaurant.adapter = customAdapter2

        // 제주 투어
        val tourLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.viewRecyclerTour.layoutManager = tourLayoutManager
        val customAdapter3 = RecyclerView(datasTour)
        binding.viewRecyclerTour.adapter = customAdapter3

//        // 제주 축제
//        val festivalLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        binding.viewRecyclerFestival.layoutManager = festivalLayoutManager
//        val customAdapter4 = RecyclerView(datasFestival)
//        binding.viewRecyclerFestival.adapter = customAdapter4

//        // 제주 쇼핑
//        val shoppingLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        binding.viewRecyclerShopping.layoutManager = shoppingLayoutManager
//        val customAdapter5 = RecyclerView(datasShopping)
//        binding.viewRecyclerShopping.adapter = customAdapter5



       /* // 메인 슬라이더
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)

        // 뷰페이저2 어댑터 설정하기
        val images = listOf(R.drawable.jeju_apec01, R.drawable.jeju_apec02, R.drawable.jeju_apec03, R.drawable.jeju_apec04)
        val imageSliderAdapter = ImageSliderAdapter(images)
        viewPager.adapter = imageSliderAdapter*/

        viewPager_mainVisual = findViewById(R.id.viewPager_mainVisual)
        viewPager_mainVisual.adapter = ImageSliderAdapter(getMainvisual()) // 어댑터 생성
        viewPager_mainVisual.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로





        // Bottom Navigation link
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.home -> {
                    // 홈 아이템 클릭 처리
                    val intent = Intent(this@MainActivity, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.chat -> {
                    val intent = Intent(this@MainActivity, ChatActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.youtube -> {
                    openWebPage("https://www.youtube.com/c/visitjeju")
                    true
                }
                R.id.instagram -> {
                    openWebPage("https://www.instagram.com/visitjeju.kr")
                    true
                }
                else -> false
            }
        }


    } //onCreate

    // 위치 데이터 획득 추가 ---------------------------------------------------------
    private fun createLocationRequest() {
        locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }!!
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations){
                    // 여기서 위치 정보를 사용하세요.
                    sendLocationToServer(location.latitude, location.longitude)
                }
            }
        }
    }
    // -----------------------------------------------------------------------------


    // 위치 정보 업데이트 ---------------------------------------------------------------------
    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }
    //-----------------------------------------------------------------------------------------


    // 백엔드 서버로 위치 데이터 전송 -----------------------------------------------------------------------
    private fun sendLocationToServer(lat: Double, lon: Double) {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.100.104.32:8083/") // 백엔드 서버의 URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(NetworkServiceRegionNm::class.java)
        val call = service.getTourGPS(lat, lon)

        call.enqueue(object : Callback<List<TourList>> {
            override fun onResponse(call: Call<List<TourList>>, response: Response<List<TourList>>) {
                // 서버로부터 응답을 받았을 때의 처리
            }

            override fun onFailure(call: Call<List<TourList>>, t: Throwable) {
                // 통신 실패 시 처리
            }
        })
    }

    //--------------------------------------------------------------------------------------------

    // 위치 정보 업데이트 중지 -------------------------------------------------------------------------
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    //-------------------------------------------------------------------------------------------


    // 뷰 페이저에 들어갈 아이템
    private fun getMainvisual(): ArrayList<Int> {
        return arrayListOf<Int>(
            R.drawable.jeju_apec01,
            R.drawable.jeju_apec02,
            R.drawable.jeju_apec03,
            R.drawable.jeju_apec04)
    }


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
                Toast.makeText(this@MainActivity,"검색어가 전송됨 : ${query}", Toast.LENGTH_SHORT).show()
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

}

