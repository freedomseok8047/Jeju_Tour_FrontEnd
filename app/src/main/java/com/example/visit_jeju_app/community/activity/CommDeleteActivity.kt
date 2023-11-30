package com.example.visit_jeju_app.community.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.visit_jeju_app.R
import com.example.visit_jeju_app.databinding.ActivityCommDeleteBinding

class CommDeleteActivity : AppCompatActivity() {

    // View 바인딩을 위한 변수 선언
    lateinit var binding : ActivityCommDeleteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // View 바인딩 초기화
        binding = ActivityCommDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent로 전달받은 게시물의 문서 ID 가져오기
        val DocId = intent.getStringExtra("DocId")

        // 게시물의 문서 ID가 전달되었는지 확인
        if (DocId != null) {
            // deleteDatabase 함수 호출하여 게시물 삭제
            deleteDatabase(DocId)

            // 게시물 삭제 완료 메시지 출력
            Toast.makeText(this, "게시물 삭제 완료", Toast.LENGTH_SHORT).show()

        } else {
            // 게시물의 문서 ID가 전달되지 않은 경우 에러 로그 출력
            Log.e("CommDeleteActivity", "Error")
            // 게시물 삭제 실패 메시지 출력
            Toast.makeText(this, "게시물 삭제 실패", Toast.LENGTH_SHORT).show()
        }
    }

}