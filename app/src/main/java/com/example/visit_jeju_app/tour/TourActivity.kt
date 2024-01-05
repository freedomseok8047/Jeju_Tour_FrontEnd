package com.example.visit_jeju_app.tour

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.visit_jeju_app.MainActivity
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.R
import com.example.visit_jeju_app.accommodation.AccomActivity
import com.example.visit_jeju_app.chat.ChatMainActivity
import com.example.visit_jeju_app.community.activity.CommReadActivity
import com.example.visit_jeju_app.databinding.ActivityTourBinding
import com.example.visit_jeju_app.festival.FesActivity
import com.example.visit_jeju_app.gpt.GptActivity
import com.example.visit_jeju_app.login.AuthActivity
import com.example.visit_jeju_app.restaurant.ResActivity
import com.example.visit_jeju_app.shopping.ShopActivity
import com.example.visit_jeju_app.tour.adapter.TourAdapter
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


class TourActivity : AppCompatActivity() {

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null //현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    private lateinit var handler: Handler
    private var lastUpdateTimestamp = 0L
    private val updateDelayMillis = 60000
    //리사이클러 뷰 업데이트 딜레이 업데이트 주기 생성

    lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는
    private val REQUEST_PERMISSION_LOCATION = 10

    // 페이징 설정 순서0 lsy
    // page 변수 생성
    var tourPage : Int = 0

    // 페이징 설정 순서1 lsy
    // 페이징, 레스트로 부터 전달 받을 데이터 저장할 임시 리스트
    lateinit var TourListData : MutableList<TourList>

    val recycler: RecyclerView by lazy {
        binding.recyclerView
    }

    lateinit var binding: ActivityTourBinding

    //액션버튼 토글(공통 레이아웃 코드)
    lateinit var toggle: ActionBarDrawerToggle

    // 서브메인에서 위치변경 없을 시, 백엔드에 데이터 요청 방지
    private var lastKnownLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTourBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 페이징 설정 순서2 lsy
        TourListData = mutableListOf<TourList>()

        val pref = getSharedPreferences("latlnt", MODE_PRIVATE)
        val lat : Double? = pref.getString("lat", "Default값")?.toDoubleOrNull()
        val lnt : Double? = pref.getString("lnt", "Default값")?.toDoubleOrNull()
        Log.d("ljs", "SharedPreferences에 현재위치 불러오기 ${lat}, ${lnt}")


        // 공통 레이아웃 시작 -------------------------------------------------------------
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
            ActionBarDrawerToggle(this@TourActivity, binding.drawerLayout, R.string.open, R.string.close)
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

        // Bottom Navigation link(공통 레이아웃 코드)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.home -> {
                    // 홈 아이템 클릭 처리
                    val intent = Intent(this@TourActivity, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.chat -> {
                    val intent = Intent(this@TourActivity, GptActivity::class.java)
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
        // 공통 레이아웃 끝 -------------------------------------------------------------

        handler = Handler(Looper.getMainLooper())

        mLocationRequest = LocationRequest.create().apply {

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 30000
            //초기화 시간 30초

        }

        if (checkPermissionForLocation(this)) {
            startLocationUpdates()

        }

        binding.pageChange.setOnClickListener {
            val intent = Intent(this@TourActivity, TourRegionNmActivity::class.java)
            startActivity(intent)
        }

        // 페이징 설정 순서5
        // RecyclerView에 스크롤 리스너 추가(맨 아래에 닿았을 때, page 1씩 증가)
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) { // 목록의 끝에 도달했는지 확인
                    tourPage++ // 페이지 번호 증가
                    getTourListWithinRadius2(lat, lnt, 7.0, tourPage) // 서버에 새 페이지 데이터 요청
                    Log.d("lsy", "Requesting page 확인1: $tourPage")
                }
            }
        })

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

    // 서브메인에서 위치변경 없을 시, 백엔드에 데이터 요청 방지
    private fun onLocationChanged(location: Location) {
        if (lastKnownLocation == null || isLocationChanged(location, lastKnownLocation!!)) {
        mLastLocation = location
        lastKnownLocation = location
            // 페이징 설정 순서6
            val pref = getSharedPreferences("latlnt", MODE_PRIVATE)
            val lat: Double? = pref.getString("lat", null)?.toDoubleOrNull()
            val lnt: Double? = pref.getString("lnt", null)?.toDoubleOrNull()

        getTourListWithinRadius(lat, lnt, 7.0, tourPage)
    }
   }

    // 서브메인에서 위치변경 없을 시, 백엔드에 데이터 요청 방지
    private fun isLocationChanged(newLocation: Location, lastLocation: Location): Boolean {
        return newLocation.latitude != lastLocation.latitude || newLocation.longitude != lastLocation.longitude
    }

    private fun getTourListWithinRadius(lat: Double?, lnt: Double?, radius : Double, tourPage : Int) {

        val networkService = (applicationContext as MyApplication).networkService
        val tourListCall = networkService.getTourGPS(lat, lnt, radius , tourPage)

        tourListCall.enqueue(object : Callback<MutableList<TourList>> {
            override fun onResponse(
                call: Call<MutableList<TourList>>,
                response: Response<MutableList<TourList>>

            ) {
                // 페이징 설정 순서3 lsy
                if (response.isSuccessful) {
                    val tourList = response.body()
                    tourList?.let {
                        Log.d("lsy", "getTourListWithinRadius1으로 불러온 tourList 값 : ${tourList}")
                        Log.d("lsy", "getTourListWithinRadius1으로 불러온 tourList 사이즈 : ${tourList.size}")
                        Log.d("lsy", "통신 후 받아온 tourList 길이 값 : ${tourList.size}")
                        Log.d("lsy", "Requesting tourPage 확인2: $tourPage")
                        // 받아온 데이터를 임시로 저장할 리스트를 전역 하나 만들고,
                        // 최초로 5개를 받아와서, 전역에 넣고,
                        // 페이징 되서, 2번째 페이지의 데이터 5개를 받아오면, 그 데이터를
                        // 다시, 전역에 선언한 리스트에 다시 담고
                        // 어댑터에 연결하기, 어댑터 객체에 다시 리스트를 인자로 넣고
                        // 데이터 변경 , 데이터를 추가 했을 때, ->

                        TourListData.addAll(it)

                        val currentTime = System.currentTimeMillis()

                        // 일정 시간이 지나지 않았으면 업데이트를 건너뜁니다.
                        if (currentTime - lastUpdateTimestamp < updateDelayMillis) {
                            return
                        }

                        lastUpdateTimestamp = currentTime

                        val layoutManager = LinearLayoutManager(this@TourActivity)

                        binding.recyclerView.layoutManager = layoutManager

                        binding.recyclerView.adapter =
                            TourAdapter(this@TourActivity, TourListData)

//                        binding.recyclerView1234.addItemDecoration(
//                            DividerItemDecoration(this@TourActivity, LinearLayoutManager.VERTICAL)
//                        )

                    }
                }
            }
            override fun onFailure(call: Call<MutableList<TourList>>, t: Throwable) {
                Log.d("ljs", "fail")
                call.cancel()
            }
        })
    }

    // 페이징 설정 순서4 lsy
    private fun getTourListWithinRadius2(lat: Double?, lnt: Double?, radius : Double, tourPage : Int) {
        Log.d("lsy", "getTourListWithinRadius2 실행")
        val networkService = (applicationContext as MyApplication).networkService
        val tourListCall = networkService.getTourGPS(lat, lnt, radius , tourPage)

        tourListCall.enqueue(object : Callback<MutableList<TourList>> {
            override fun onResponse(
                call: Call<MutableList<TourList>>,
                response: Response<MutableList<TourList>>

            ) {
                if (response.isSuccessful) {
                    val tourList = response.body()
                    tourList?.let {
                        Log.d("lsy", "getTourListWithinRadius2로 불러온 새 tourList 값 : ${tourList}")
                        Log.d("lsy", "getTourListWithinRadius2로 불러온 새 tourList 사이즈 : ${tourList.size}")
                        Log.d("lsy", "통신 후 받아온 tourList 길이 값 : ${tourList.size}")
                        Log.d("lsy", "Requesting tourPage 확인2: $tourPage")

                        getData2(it)

                        val currentTime = System.currentTimeMillis()

                        // 일정 시간이 지나지 않았으면 업데이트를 건너뜁니다.
                        if (currentTime - lastUpdateTimestamp < updateDelayMillis) {
                            return
                        }
                        lastUpdateTimestamp = currentTime


                        val layoutManager = LinearLayoutManager(this@TourActivity)

                        binding.recyclerView.layoutManager = layoutManager

                        binding.recyclerView.adapter =
                            TourAdapter(this@TourActivity, TourListData)

//                        binding.recyclerView1234.addItemDecoration(
//                            DividerItemDecoration(this@TourActivity, LinearLayoutManager.VERTICAL)
//                        )

                    }
                }
            }
            override fun onFailure(call: Call<MutableList<TourList>>, t: Throwable) {
                Log.d("ljs", "fail")
                call.cancel()
            }
        })
    }

    fun getData2(datas2: MutableList<TourList>?) {
        Log.d("lsy","getData2 함수 호출 시작.")
        Log.d("lsy","getData2 함수 호출 시작2.datasSpring size 값 : ${TourListData?.size} ")
        TourListData?.size?.let {
            recycler.adapter?.notifyItemInserted(
                it.minus(1)
            )
        }
        if (TourListData?.size != null){
            TourListData?.addAll(datas2 as Collection<TourList>)
        }
        recycler.adapter?.notifyDataSetChanged()

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
