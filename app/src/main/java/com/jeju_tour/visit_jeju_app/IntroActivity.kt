package com.jeju_tour.visit_jeju_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

import com.jeju_tour.visit_jeju_app.login.AuthActivity

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.jeju_tour.visit_jeju_app.R.layout.activity_intro)

        if (com.jeju_tour.visit_jeju_app.MyApplication.Companion.checkAuth()) {
            // 로그인 상태인 경우 MainActivity로 이동
            val mainIntent = Intent(this, com.jeju_tour.visit_jeju_app.MainActivity::class.java)
            startActivity(mainIntent)
        } else {
            // 로그인 상태가 아닌 경우 SignInActivity로 이동
            val signInIntent = Intent(this, AuthActivity::class.java)
            startActivity(signInIntent)
        }
        finish() // 현재 액티비티 종료

    }
}