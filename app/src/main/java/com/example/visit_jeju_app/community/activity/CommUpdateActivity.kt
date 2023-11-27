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

        // 수정
        binding.CommunityModify.setOnClickListener {
            val data = mapOf(
                "title" to binding.regTitle.text.toString(),
                "content" to binding.regTitle.text.toString(),
                "date" to dateToString(Date())
            )
            if (docId != null) {
                MyApplication.db.collection("Communities").document(docId).update(data)
            }
            overridePendingTransition(0, 0) //인텐트 효과 없애기
            val intent = intent //인텐트
            startActivity(intent) //액티비티 열기
            overridePendingTransition(0, 0) //인텐트 효과 없애기
            finish()
        }

        // 수정 취소
        binding.CommunityCancel.setOnClickListener {
            finish()
        }
    }

}