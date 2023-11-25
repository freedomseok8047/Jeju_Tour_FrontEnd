package com.example.visit_jeju_app.community.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.R
import com.example.visit_jeju_app.community.activity.CommWriteActivity
import com.example.visit_jeju_app.community.model.CommunityData
import com.example.visit_jeju_app.community.myCheckPermission
import com.example.visit_jeju_app.community.recycler.CommunityAdapter
import com.example.visit_jeju_app.databinding.ActivityCommReadBinding
import com.google.firebase.firestore.Query

class CommReadActivity : AppCompatActivity() {

    lateinit var binding : ActivityCommReadBinding

    //액션버튼 토글
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommReadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        //드로워화면 액션버튼 클릭 시 드로워 화면 나오게 하기
        toggle =
            ActionBarDrawerToggle(this@CommReadActivity, binding.drawerLayout,R.string.open, R.string.close)

        binding.drawerLayout.addDrawerListener(toggle)
        //화면 적용하기
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        //버튼 클릭스 동기화 : 드로워 열어주기
        toggle.syncState()


        myCheckPermission(this)
        makeRecyclerView()

        binding.add.setOnClickListener {
            startActivity(Intent(this, CommWriteActivity::class.java))
        }

    }
    private fun makeRecyclerView(){
        MyApplication.db.collection("Communities")
            .orderBy("date", Query.Direction.DESCENDING) // date 필드를 기준으로 내림차순 정렬
            .get()
            .addOnSuccessListener {result ->
                val itemList = mutableListOf<CommunityData>()
                for(document in result){
                    val item = document.toObject(CommunityData::class.java)
                    item.docId=document.id
                    itemList.add(item)
                }
                binding.communityRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.communityRecyclerView.adapter = CommunityAdapter(this, itemList)
            }
            .addOnFailureListener{exception ->
                Log.d("lhs", "error.. getting document..", exception)
                Toast.makeText(this, "서버 데이터 획득 실패", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


}