package com.example.visit_jeju_app.chat

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.visit_jeju_app.login.model.User
import com.example.visit_jeju_app.login.recycler.UserAdapter
import com.example.visit_jeju_app.databinding.ActivityChatMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatMainActivity : AppCompatActivity() {

    lateinit var binding: ActivityChatMainBinding
    lateinit var adapter: UserAdapter

    private lateinit var userList: ArrayList<User>
    private lateinit var auth: FirebaseAuth
    private lateinit var rdb: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        val allowedEmails = listOf("xoqls081215@gmail.com", "")
        val currentUser = auth.currentUser

        if (currentUser != null && allowedEmails.contains(currentUser.email)) {
            // 두 사용자 이름을 리스트로 만들고 각 사용자에 대해 쿼리를 실행합니다.
            val targetUsernames = listOf("rlkjsdl", "")
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
}