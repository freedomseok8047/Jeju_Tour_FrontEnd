package com.example.visit_jeju_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.visit_jeju_app.login.AuthActivity
import com.example.visit_jeju_app.login.SignInActivity

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        if (MyApplication.checkAuth()) {
            // 로그인 상태인 경우 MainActivity로 이동
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        } else {
            // 로그인 상태가 아닌 경우 SignInActivity로 이동
            val signInIntent = Intent(this, AuthActivity::class.java)
            startActivity(signInIntent)
        }
        finish() // 현재 액티비티 종료

    }
}