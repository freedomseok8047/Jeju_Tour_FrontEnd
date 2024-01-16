package com.example.visit_jeju_app.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.R
import com.example.visit_jeju_app.accommodation.AccomActivity
import com.example.visit_jeju_app.community.activity.CommReadActivity
import com.example.visit_jeju_app.login.model.User
import com.example.visit_jeju_app.login.recycler.UserAdapter
import com.example.visit_jeju_app.databinding.ActivityChatMainBinding
import com.example.visit_jeju_app.festival.FesActivity
import com.example.visit_jeju_app.login.AuthActivity
import com.example.visit_jeju_app.restaurant.ResActivity
import com.example.visit_jeju_app.shopping.ShopActivity
import com.example.visit_jeju_app.tour.TourActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatMainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    lateinit var binding: ActivityChatMainBinding
    lateinit var adapter: UserAdapter

    private lateinit var userList: ArrayList<User>
    private lateinit var auth: FirebaseAuth
    private lateinit var rdb: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        //드로워화면 액션버튼 클릭 시 드로워 화면 나오게 하기
        toggle =
            ActionBarDrawerToggle(this@ChatMainActivity, binding.drawerLayout, R.string.open, R.string.close)

        binding.drawerLayout.addDrawerListener(toggle)
        //화면 적용하기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //버튼 클릭스 동기화 : 드로워 열어주기
        toggle.syncState()

        // NavigationView 메뉴 아이템 클릭 리스너 설정
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
                    startActivity(Intent(this, ChatMainActivity::class.java))
                    true
                }

                else -> false
            }
        }

        //초기화
        auth = Firebase.auth
        rdb = Firebase.database.reference
        userList = ArrayList()

        adapter = UserAdapter(this, userList)
        binding.userRecyclerView.layoutManager = LinearLayoutManager(this)

        // RecyclerView에 ItemAnimator 추가
        val itemAnimator = DefaultItemAnimator()
        binding.userRecyclerView.itemAnimator = itemAnimator

        binding.userRecyclerView.adapter = adapter


        // 채팅 화면에 나올 유저 뷰

        // 해당 이메일의 유저로 로그인을 하면 필터링이 됨.
        val allowedEmails = listOf("xoqls081215@gmail.com", "tb081212@naver.com")
        val currentUser = auth.currentUser

        if (currentUser != null && !allowedEmails.contains(currentUser.email)) {
            // 두 사용자 이름을 리스트로 만들고 각 사용자에 대해 쿼리를 실행합니다.
            val targetUsernames = listOf("제주시 고객센터","서귀포시 고객센터", "")
            for (username in targetUsernames) {
                rdb.child("user").orderByChild("username").equalTo(username)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        @SuppressLint("RestrictedApi")
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (userSnapshot in snapshot.children) {
                                val user = userSnapshot.getValue(User::class.java)
                                if (user != null) {
                                    userList.add(user)
                                }
                            }
                            adapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // 에러 처리를 수행합니다.
                        }
                    })
            }
        }else{
            // 전체 유저가 나오게 함
            rdb.child("user").addListenerForSingleValueEvent(object : ValueEventListener {
                    @SuppressLint("RestrictedApi")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        userList.clear()

                        for (userSnapshot in snapshot.children) {
                            // 특정 username에 해당하는 사용자 정보를 가져옵니다.
                            val user = userSnapshot.getValue(User::class.java)

                            if (user != null) {
                                userList.add(user)
                            }
                        }

                        adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // 에러 처리를 수행합니다.
                    }
                })
        }

    } //onCreate

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
                Toast.makeText(this@ChatMainActivity,"검색어가 전송됨 : ${query}", Toast.LENGTH_SHORT).show()
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }
}