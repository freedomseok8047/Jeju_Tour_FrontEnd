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

        //특정 유저만 가져오게 하기
//        val targetUsername = "ktb" // 가져올 사용자의 username
//
//        rdb.child("user").orderByChild("username").equalTo(targetUsername)
//            .addListenerForSingleValueEvent(object : ValueEventListener {

        //전체 유저 가져오기
//        rdb.child("user").addListenerForSingleValueEvent(object : ValueEventListener {

        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.email == "xoqls081215@gmail.com") {
            val targetUsername = "rlkjsdl" // 가져올 사용자의 username

            rdb.child("user").orderByChild("username").equalTo(targetUsername)
                .addListenerForSingleValueEvent(object : ValueEventListener {
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
        }else{
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