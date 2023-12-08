package com.example.visit_jeju_app.community.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.visit_jeju_app.MainActivity
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.R
import com.example.visit_jeju_app.accommodation.AccomActivity
import com.example.visit_jeju_app.chat.ChatActivity
import com.example.visit_jeju_app.community.activity.CommWriteActivity
import com.example.visit_jeju_app.community.model.CommunityData
import com.example.visit_jeju_app.community.myCheckPermission
import com.example.visit_jeju_app.community.recycler.CommunityAdapter
import com.example.visit_jeju_app.databinding.ActivityCommReadBinding
import com.example.visit_jeju_app.festival.FesActivity
import com.example.visit_jeju_app.gpt.GptActivity
import com.example.visit_jeju_app.login.AuthActivity
import com.example.visit_jeju_app.main.adapter.ImageSliderAdapter
import com.example.visit_jeju_app.restaurant.ResActivity
import com.example.visit_jeju_app.shopping.ShopActivity
import com.example.visit_jeju_app.tour.TourActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat

class CommReadActivity : AppCompatActivity() {

    lateinit var binding : ActivityCommReadBinding

    //액션버튼 토글(공통 레이아웃 코드)
    lateinit var toggle: ActionBarDrawerToggle

    // activity_comm_read.xml 상단 비주얼
    lateinit var viewPager_communityVisual: ViewPager2


    // crud된 파이어베이스 데이터가 activiy_comm_read.xml 뷰에 자동반영되도록 하는 코드
    // Firebase에서 데이터가 업데이트될 때 RecyclerView에 자동으로 반영되도록 하는 코드
    lateinit var communityAdapter: CommunityAdapter
    lateinit var listenerRegistration: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommReadBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            ActionBarDrawerToggle(this@CommReadActivity, binding.drawerLayout,R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)

        //화면 적용하기(공통 레이아웃 코드)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //버튼 클릭스 동기화 : 드로워 열어주기(공통 레이아웃 코드)
        toggle.syncState()

        // NavigationView 메뉴 아이템 클릭 리스너 설정(공통 레이아웃 코드)
        binding.mainDrawerView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.accommodation -> {
                    startActivity(Intent(this, AccomActivity::class.java))
                    true
                }
                R.id.restaurant -> {
                    startActivity(Intent(this, ResActivity::class.java))
                    true
                }
                R.id.tour -> {
                    startActivity(Intent(this, TourActivity::class.java))
                    true
                }
                R.id.festival -> {
                    startActivity(Intent(this, FesActivity::class.java))
                    true
                }
                R.id.shopping -> {
                    startActivity(Intent(this, ShopActivity::class.java))
                    true
                }
                R.id.community -> {
                    // '커뮤니티' 메뉴 아이템 클릭 시 CommReadActivity로 이동
                    startActivity(Intent(this, CommReadActivity::class.java))
                    true
                }
                R.id.chatting -> {
                    startActivity(Intent(this, ChatActivity::class.java))
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
                    val intent = Intent(this@CommReadActivity, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.chat -> {
                    val intent = Intent(this@CommReadActivity, GptActivity::class.java)
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


        // activity_comm_read.xml 상단 비주얼
        viewPager_communityVisual = findViewById(R.id.viewPager_communityVisual)
        viewPager_communityVisual.adapter = ImageSliderAdapter(getCommunityvisual()) // 어댑터 생성
        viewPager_communityVisual.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로


        myCheckPermission(this)
        makeRecyclerView()

        binding.add.setOnClickListener {
            startActivity(Intent(this, CommWriteActivity::class.java))
        }

    } // onCreate

    // 함수 구현 ---------------------------------------------------------------------------

    // Bottom Navigation link(공통 레이아웃 코드)
    private fun openWebPage(url: String) {
        val webpage = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
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
                Toast.makeText(this@CommReadActivity,"검색어가 전송됨 : ${query}", Toast.LENGTH_SHORT).show()
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    // activity_comm_read.xml 상단 비주얼
    // 뷰 페이저에 들어갈 아이템
    private fun getCommunityvisual(): ArrayList<Int> {
        return arrayListOf<Int>(
            R.drawable.jejuimage1,
            R.drawable.jejuimage2,
            R.drawable.jejuimage3,
            R.drawable.jejuimage4)
    }

    private fun makeRecyclerView() {

        // crud된 파이어베이스 데이터가 activiy_comm_read.xml 뷰에 자동반영되도록 하는 코드
        communityAdapter = CommunityAdapter(this, mutableListOf())
        binding.communityRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.communityRecyclerView.adapter = communityAdapter

        // crud된 파이어베이스 데이터가 activiy_comm_read.xml 뷰에 자동반영되도록 하는 코드
        // 데이터 변경 감지 리스너 등록
        listenerRegistration = MyApplication.db.collection("Communities")
            .orderBy("date", Query.Direction.DESCENDING) // date 필드를 기준으로 내림차순 정렬

            // crud된 파이어베이스 데이터가 activiy_comm_read.xml 뷰에 자동반영되도록 하는 코드
            .addSnapshotListener { result, exception ->
                if (exception != null) {
                    Log.d("lhs", "error.. getting document..", exception)
                    Toast.makeText(this, "서버 데이터 획득 실패", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                val itemList = mutableListOf<CommunityData>()
                // crud된 파이어베이스 데이터가 activiy_comm_read.xml 뷰에 자동반영되도록 하는 코드
                for (document in result!!) {
                    val item = document.toObject(CommunityData::class.java)
                    item.docId = document.id
                    itemList.add(item)
                }
                // crud된 파이어베이스 자료 가져올 때 최신순으로 내림차순 정렬되는 코드
                communityAdapter.updateData(itemList)
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // crud된 파이어베이스 데이터가 activiy_comm_read.xml 뷰에 자동반영되도록 하는 코드
    override fun onDestroy() {
        // 액티비티 종료 시 리스너 해제
        listenerRegistration.remove()
        super.onDestroy()
    }

}