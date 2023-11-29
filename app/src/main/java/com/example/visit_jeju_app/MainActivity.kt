package com.example.visit_jeju_app


import android.content.Intent
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
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.visit_jeju_app.accommodation.adapter.AccomAdapter_Main
import com.example.visit_jeju_app.accommodation.model.AccomList
import com.example.visit_jeju_app.chat.ChatActivity
import com.example.visit_jeju_app.databinding.ActivityMainBinding
import com.example.visit_jeju_app.main.adapter.ImageSliderAdapter
import com.example.visit_jeju_app.main.adapter.RecyclerView
import com.example.visit_jeju_app.restaurant.adapter.ResAdapter_Main
import com.example.visit_jeju_app.restaurant.model.ResList
import com.example.visit_jeju_app.tour.adapter.TourAdapter_Main
import com.example.visit_jeju_app.tour.model.TourList
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    //액션버튼 토글
    lateinit var toggle: ActionBarDrawerToggle

    lateinit var viewPager_mainVisual: ViewPager2

    // 통신으로 받아온 투어 정보 담는 리스트 , 전역으로 설정, 각 어느 곳에서든 사용가능.
    // 제주 숙박
    lateinit var dataListFromAccomActivity: MutableList<AccomList>
    // 제주 맛집
    lateinit var dataListFromResActivity: MutableList<ResList>
    // 제주 투어
    lateinit var dataListFromTourActivity: MutableList<TourList>
    // 제주 축제
    //lateinit var dataListFromFesActivity: MutableList<FesList>
    // 제주 쇼핑
    //lateinit var dataListFromShopActivity: MutableList<ShopList>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 각 가테고리별 넘어온 데이터 담을 리스트 초기화, 할당.
        dataListFromAccomActivity = mutableListOf<AccomList>()
        dataListFromResActivity = mutableListOf<ResList>()
        dataListFromTourActivity = mutableListOf<TourList>()
        //dataListFromFesActivity = mutableListOf<FesList>()
        //dataListFromShopActivity = mutableListOf<ShopList>()


        // ========== 레이아웃 관련 ========== //

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


        // ========== 각 카테고리 별 데이터 불러오기 ========== //

        val networkService = (applicationContext as MyApplication).networkService

        // 제주 숙박
        val accomListCall = networkService.GetAccomList()

        accomListCall.enqueue(object : Callback<List<AccomList>> {
            override fun onResponse(
                call: Call<List<AccomList>>,
                accomponse: Response<List<AccomList>>

            ) {
                val accomList = accomponse.body()

                Log.d("ljs","accomModel 값 : ${accomList}")

                //데이터 받기 확인 후, 리스트에 담기.

                accomList?.get(0)?.let { dataListFromAccomActivity.add(it) }
                accomList?.get(1)?.let { dataListFromAccomActivity.add(it) }
                accomList?.get(2)?.let { dataListFromAccomActivity.add(it) }
                accomList?.get(3)?.let { dataListFromAccomActivity.add(it) }
                accomList?.get(4)?.let { dataListFromAccomActivity.add(it) }
                Log.d("lsy","test 값 추가 후 확인 : dataListFromTourActivity 길이 값 : ${dataListFromAccomActivity?.size}")
                val accomLayoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                binding.viewRecyclerAccom.layoutManager = accomLayoutManager
                binding.viewRecyclerAccom.adapter = AccomAdapter_Main(this@MainActivity,dataListFromAccomActivity)
            }

            override fun onFailure(call: Call<List<AccomList>>, t: Throwable) {
                Log.d("lsy", "fail")
                call.cancel()
            }
        })

        // 제주 맛집
        val resListCall = networkService.GetResList()

        resListCall.enqueue(object : Callback<List<ResList>> {
            override fun onResponse(
                call: Call<List<ResList>>,
                response: Response<List<ResList>>

            ) {
                val resList = response.body()

                Log.d("ljs","resModel 값 : ${resList}")

                //데이터 받기 확인 후, 리스트에 담기.

                resList?.get(0)?.let { dataListFromResActivity.add(it) }
                resList?.get(1)?.let { dataListFromResActivity.add(it) }
                resList?.get(2)?.let { dataListFromResActivity.add(it) }
                resList?.get(3)?.let { dataListFromResActivity.add(it) }
                resList?.get(4)?.let { dataListFromResActivity.add(it) }
                Log.d("lsy","test 값 추가 후 확인 : dataListFromResActivity 길이 값 : ${dataListFromResActivity?.size}")
                val resLayoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                binding.viewRecyclerRestaurant.layoutManager = resLayoutManager
                binding.viewRecyclerRestaurant.adapter = ResAdapter_Main(this@MainActivity,dataListFromResActivity)
            }

            override fun onFailure(call: Call<List<ResList>>, t: Throwable) {
                Log.d("lsy", "fail")
                call.cancel()
            }
        })


        // 제주 투어
        val tourListCall = networkService.GetTourList()

        tourListCall.enqueue(object : Callback<List<TourList>> {
            override fun onResponse(
                call: Call<List<TourList>>,
                response: Response<List<TourList>>

            ) {
                val tourList = response.body()

                Log.d("lsy","tourModel 값 : ${tourList}")

                //데이터 받기 확인 후, 리스트에 담기.
                // 전체 623개
//                dataListFromTourActivity?.addAll(tourList as Collection<TourList>)
                tourList?.get(0)?.let { dataListFromTourActivity.add(it) }
                tourList?.get(1)?.let { dataListFromTourActivity.add(it) }
                tourList?.get(2)?.let { dataListFromTourActivity.add(it) }
                tourList?.get(3)?.let { dataListFromTourActivity.add(it) }
                tourList?.get(4)?.let { dataListFromTourActivity.add(it) }
                Log.d("lsy","test 값 추가 후 확인 : dataListFromTourActivity 길이 값 : ${dataListFromTourActivity?.size}")
                val tourLayoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                binding.viewRecyclerTour.layoutManager = tourLayoutManager
                binding.viewRecyclerTour.adapter = TourAdapter_Main(this@MainActivity,dataListFromTourActivity)
            }

            override fun onFailure(call: Call<List<TourList>>, t: Throwable) {
                Log.d("lsy", "fail")
                call.cancel()
            }
        })









        // 메인 비주얼
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

