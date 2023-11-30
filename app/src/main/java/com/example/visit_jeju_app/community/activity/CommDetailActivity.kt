package com.example.visit_jeju_app.community.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.R
import com.example.visit_jeju_app.accommodation.AccomActivity
import com.example.visit_jeju_app.chat.ChatActivity
import com.example.visit_jeju_app.community.model.CommunityData
import com.example.visit_jeju_app.community.recycler.CommentAdapter
import com.example.visit_jeju_app.databinding.ActivityCommDetailBinding
import com.example.visit_jeju_app.festival.FesActivity
import com.example.visit_jeju_app.login.AuthActivity
import com.example.visit_jeju_app.restaurant.ResActivity
import com.example.visit_jeju_app.shopping.ShopActivity
import com.example.visit_jeju_app.tour.TourActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat

class CommDetailActivity : AppCompatActivity() {

    lateinit var binding : ActivityCommDetailBinding

    // 공통 메인 레이아웃 적용 코드
    //액션버튼 토글
    lateinit var toggle: ActionBarDrawerToggle

    data class comment(val comment : String, val time : String)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //(공통 레이아웃 코드)
        val headerView = binding.mainDrawerView.getHeaderView(0)
        val headerUserEmail = headerView.findViewById<TextView>(R.id.headerUserEmail)
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

        val userEmail1 = intent.getStringExtra("USER_EMAIL") ?: "No Email"
        headerUserEmail.text = userEmail1

        // 공통 메인 레이아웃 적용 코드
        setSupportActionBar(binding.toolbar)

        //드로워화면 액션버튼 클릭 시 드로워 화면 나오게 하기
        toggle =
            ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,  // 세 번째 매개변수로 툴바 전달
                R.string.open,
                R.string.close
            )
        binding.drawerLayout.addDrawerListener(toggle)

        //화면 적용하기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //버튼 클릭스 동기화 : 드로워 열어주기
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

        val docId = intent.getStringExtra("DocId")
        val title = intent.getStringExtra("CommunityTitle")
        val content = intent.getStringExtra("CommunityContent")
        val date = intent.getStringExtra("CommunityDate")

        binding.CommunityTitle.text = title
        binding.CommunityDate.text = date
        binding.CommunityContent.text = content

        var commentList = mutableListOf<comment>()
        var count = 0
        if (docId != null) {
            MyApplication.db.collection("Communities").document(docId).collection("Comments")
                .get()
                .addOnSuccessListener { result ->
                    val itemList = mutableListOf<CommunityData>()
                    for (document in result) {
                        commentList.add(comment(document.data.get("comment").toString(), document.data.get("timestamp").toString()))
                        count++
                        if(result.size() == count) {
                            Log.d("lhs", "$commentList")
                            binding.commentRecyclerView.layoutManager = LinearLayoutManager(this)
                            binding.commentRecyclerView.adapter = CommentAdapter(this, commentList)
                        }
                    }

                }
        }

        // 파이어베이스 writeEmail 필드에 저장된 데이터를 "작성자" 부분에 불러오는 코드
        if (docId != null) {
            MyApplication.db.collection("Communities").document(docId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val writerEmail = documentSnapshot.getString("writerEmail")
                        Log.d("CommDetailActivity", "Writer Email: $writerEmail")
                        binding.CommunityWriter.text = writerEmail

                        // 디테일 뷰에서 현재 로그인계정과 해당 글 작성자 일치여부에 따른 "수정","삭제"버튼 숨김여부 관련 코드
                        // 현재 로그인한 사용자와 작성자가 같으면 수정, 삭제 버튼을 표시
                        checkCurrentUserAndWriter(FirebaseAuth.getInstance().currentUser?.email, writerEmail)

                    } else {
                        Log.d("CommDetailActivity", "해당 문서가 없습니다")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("CommDetailActivity", "데이터 가져오기 실패: ", exception)
                }
        }

        // 파이어베이스에 저장된 카테고리 데이터를 디테일 뷰에 불러오는 관련 코드
        // Firebase에서 카테고리 가져와서 TextView에 설정
        if (docId != null) {
            MyApplication.db.collection("Communities").document(docId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val category = documentSnapshot.getString("category") ?: ""
                        binding.itemCategoryView.text = category
                    } else {
                        Log.d("CommDetailActivity", "해당 문서가 없습니다")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("CommDetailActivity", "데이터 가져오기 실패: ", exception)
                }
        }


        // 수정
        binding.CommunityModify.setOnClickListener {
            val intent = Intent(this, CommUpdateActivity::class.java)
            intent.putExtra("DocId", docId)
            intent.putExtra("CommunityTitle", title)
            intent.putExtra("CommunityContent", content)
            intent.putExtra("CommunityDate", date)
            overridePendingTransition(0, 0) //인텐트 효과 없애기
            startActivity(intent) //액티비티 열기
            overridePendingTransition(0, 0) //인텐트 효과 없애기
            finish()
        }

        // 삭제
        binding.CommunityDelete.setOnClickListener {
            if (docId != null) {
                MyApplication.db.collection("Communities").document(docId)
                    .delete()

                    // detail 뷰에서 '삭제' 버튼 클릭 시, read 뷰로 이동하도록 설정 코드
                    .addOnSuccessListener {
                        // 삭제 성공 시 CommReadActivity로 이동
                        val intent = Intent(this, CommReadActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        // 삭제 실패 시 메시지 출력
                        Log.e("CommDetailActivity", "Error deleting document", it)
                        // 원하는 에러 처리 로직을 여기에 추가할 수 있습니다.
                    }
            }
        }

        // 등록한 이미지 가져 오기
        val imgRef = MyApplication.storage.reference.child("images/${docId}.jpg")
        imgRef.downloadUrl.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(this)
                    .load(task.result)
                    .into(binding.ImageView)
            }
        }

        val datenow = SimpleDateFormat("yyyy-MM-dd HH:mm")
        // 댓글 등록
        binding.CommentWrite.setOnClickListener {
            val commentData = mapOf(
                "comment" to binding.CommunityComment.text.toString(),
                "timestamp" to datenow.format(System.currentTimeMillis()).toString()
            )
            if (docId != null) {
                MyApplication.db.collection("Communities").document(docId)
                    .collection("Comments").add(commentData)
            }
            finish()
            overridePendingTransition(0, 0) //인텐트 효과 없애기
            val intent = intent //인텐트
            startActivity(intent) //액티비티 열기
            overridePendingTransition(0, 0) //인텐트 효과 없애기
        }
    } //onCreate

    // 함수 구현 ---------------------------------------------------------------------------

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
                Toast.makeText(this@CommDetailActivity,"검색어가 전송됨 : ${query}", Toast.LENGTH_SHORT).show()
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    // 디테일 뷰에서 현재 로그인계정과 해당 글 작성자 일치여부에 따른 "수정","삭제"버튼 숨김여부 관련 코드
    private fun checkCurrentUserAndWriter(currentUserEmail: String?, writerEmail: String?) {
        if (currentUserEmail == writerEmail) {
            // 현재 로그인한 사용자와 작성자가 같으면 수정, 삭제 버튼을 표시
            binding.CommunityModify.visibility = View.VISIBLE
            binding.CommunityDelete.visibility = View.VISIBLE
        } else {
            // 다르면 숨김
            binding.CommunityModify.visibility = View.GONE
            binding.CommunityDelete.visibility = View.GONE
        }
    }


    // 파이어베이스에 저장된 timestamp형인 date를 불러올 수 있도록
    // activity_comm_detail.xml 내 android:id="@+id/CommunityDate"인 textview에 timestamp형인 date를 설정
    private fun setCommunityDate(timestamp: Timestamp) {
        binding.CommunityDate.text = timestampToString(timestamp)
    }

    // Timestamp를 문자열로 변환하는 함수
    private fun timestampToString(timestamp: Timestamp): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp.toDate())
    }

    // 사진 변경한 내용을 디테일 뷰와 수정 뷰 둘다 바로 반영하는 코드
    companion object {
        private const val UPDATE_REQUEST_CODE = 123
    }

}