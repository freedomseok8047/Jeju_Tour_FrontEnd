package com.example.visit_jeju_app.community.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.R
import com.example.visit_jeju_app.community.model.CommunityData
import com.example.visit_jeju_app.community.recycler.CommentAdapter
import com.example.visit_jeju_app.databinding.ActivityCommDetailBinding
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat

class CommDetailActivity : AppCompatActivity() {

    lateinit var binding : ActivityCommDetailBinding
    data class comment(val comment : String, val time : String)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

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


}