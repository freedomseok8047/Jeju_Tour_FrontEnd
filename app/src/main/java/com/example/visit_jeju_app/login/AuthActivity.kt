package com.example.visit_jeju_app.login


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.visit_jeju_app.MainActivity
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.MyApplication.Companion.auth
import com.example.visit_jeju_app.MyApplication.Companion.rdb
import com.example.visit_jeju_app.R
import com.example.visit_jeju_app.login.model.User
import com.example.visit_jeju_app.databinding.ActivityAuthBinding
import com.example.visit_jeju_app.retrofit.addUserToMysql
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding

    //    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // signInBtn 클릭 이벤트 처리
        binding.signInBtn.setOnClickListener {
            val signInIntent = Intent(this, SignInActivity::class.java)
            startActivity(signInIntent)
            finish()
        }

        // signUpBtn 클릭 이벤트 처리
        binding.signUpBtn.setOnClickListener {
            val signUpIntent = Intent(this, PhoneAuthActivity::class.java)
            startActivity(signUpIntent)
            finish()
        }


    }


}