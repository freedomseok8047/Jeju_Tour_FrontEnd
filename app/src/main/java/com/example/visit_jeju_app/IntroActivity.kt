package com.example.visit_jeju_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.visit_jeju_app.login.AuthActivity

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        // Handler를 사용하여 3초 후에 AuthActivity로 이동
        Handler().postDelayed({
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish() // IntroActivity를 종료하여 뒤로 가기 버튼을 눌러 이전 화면으로 돌아갈 수 없도록 함
        }, 3000) // 3초(3000 밀리초) 후에 실행
    }
}