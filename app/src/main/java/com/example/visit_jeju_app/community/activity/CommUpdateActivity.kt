package com.example.visit_jeju_app.community.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.R
import com.example.visit_jeju_app.community.dateToString
import com.example.visit_jeju_app.databinding.ActivityCommUpdateBinding
import java.util.Date

class CommUpdateActivity : AppCompatActivity() {

    lateinit var binding : ActivityCommUpdateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val docId = intent.getStringExtra("DocId")
        val title = intent.getStringExtra("CommunityTitle")
        val content = intent.getStringExtra("CommunityContent")
        val date = intent.getStringExtra("CommunityDate")

        binding.CommunityDate.text = date
        binding.regTitle.setText(title)
        binding.regContent.setText(content)

        // 내용 수정한 부분을 파이어베이스에 반영하는 코드
        // 수정
        binding.CommunityModify.setOnClickListener {
            val updatedTitle = binding.regTitle.text.toString()
            val updatedContent = binding.regContent.text.toString()

            // 제목 또는 내용이 수정되었는지 확인
            if (updatedTitle != title || updatedContent != content) {
                val data = mapOf(
                    "title" to updatedTitle,
                    "content" to updatedContent,
                    "date" to dateToString(Date())
                )
                if (docId != null) {
                    MyApplication.db.collection("Communities").document(docId).update(data)
                }
            }

            finish()
        }

        // 수정 취소
        binding.CommunityCancel.setOnClickListener {
            finish()
        }
    }

}