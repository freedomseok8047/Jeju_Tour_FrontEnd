package com.example.visit_jeju_app.login


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.visit_jeju_app.MainActivity
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.MyApplication.Companion.rdb
import com.example.visit_jeju_app.login.model.User
import com.example.visit_jeju_app.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityAuthBinding

    //    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth= Firebase.auth


        //MyApplication->checkAuth=>로그인이 확인
        if(MyApplication.checkAuth()){
            changeVisibility("login")
        }else {
            changeVisibility("logout")
        }

        binding.logoutBtn.setOnClickListener {
            //로그아웃...........
            MyApplication.auth.signOut()
            MyApplication.email = null
            //이메일 널로 할당
            changeVisibility("logout")
        }

        binding.goSignInBtn.setOnClickListener{
            changeVisibility("signin")
        }

        binding.signBtn.setOnClickListener {
            //이메일,비밀번호 회원가입........................
            val username = binding.authUsernameEditView.text.toString()
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()

            MyApplication.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){task ->
                    saveUser()
                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()
                    binding.authUsernameEditView.text.clear()
                    if(task.isSuccessful){
                        MyApplication.auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener{ sendTask ->
                                if(sendTask.isSuccessful){
                                    Toast.makeText(baseContext, "회원가입에서 성공, 전송된 메일을 확인해 주세요",
                                        Toast.LENGTH_SHORT).show()
                                    changeVisibility("logout")
                                    addUserToDatabase(username, email, auth.currentUser?.uid!!)
                                }else {
                                    Toast.makeText(baseContext, "메일 발송 실패", Toast.LENGTH_SHORT).show()
                                    changeVisibility("logout")
                                }
                            }
                    }else {
                        Toast.makeText(baseContext, "회원가입 실패", Toast.LENGTH_SHORT).show()
                        changeVisibility("logout")
                    }
                }
        }


        binding.loginBtn.setOnClickListener {
            // 이메일, 비밀번호 로그인
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()
            MyApplication.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()
                    if (task.isSuccessful) {
                        if (MyApplication.checkAuth()) {
                            saveEmailToSharedPreferences(email)
                            changeVisibility("login")
                            Toast.makeText(baseContext, "로그인 성공.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                        } else {
                            Toast.makeText(baseContext, "전송된 메일로 이메일 인증이 되지 않았습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(baseContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    // SharedPreferences에 이메일 저장
    fun saveEmailToSharedPreferences(email: String) {
        val sharedPref = applicationContext.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        sharedPref.edit().apply {
            putString("USER_EMAIL", email)
            apply()
        }
    }
    private fun signOut() {
        // [START auth_sign_out]
        Firebase.auth.signOut()
        // [END auth_sign_out]
    }

    private fun saveUser(){
        val data = mapOf(
            "email" to binding.authEmailEditView.text.toString(),
            "password" to binding.authPasswordEditView.text.toString(),
            "username" to binding.authUsernameEditView.text.toString(),
        )
        MyApplication.db.collection("user")
            .add(data)
            .addOnFailureListener{
                Log.d("kkang", "data save error", it)
            }
    }

    private fun addUserToDatabase(name: String, email: String, uId: String){
        rdb.child("user").child(uId).setValue(User(name, email, uId))

    }

    //매개변수를 모드라는 변수명,문자열 타입.
    fun changeVisibility(mode: String){
        if(mode === "login"){
            binding.run {
                authMainTextView.text = "${MyApplication.email} 님 반갑습니다."

                authNotEmail.visibility=View.GONE
                logoutBtn.visibility= View.VISIBLE
                goSignInBtn.visibility= View.GONE

                authUsernameEditView.visibility=View.GONE

                authEmailEditView.visibility= View.GONE
                authPasswordEditView.visibility= View.GONE
                authUsernameEditView.visibility=View.GONE
                signBtn.visibility= View.GONE
                hostSignBtn.visibility=View.GONE
                loginBtn.visibility= View.GONE
                
                googleSignBtn.visibility=View.GONE
            }

        }else if(mode === "logout"){
            binding.run {
                authMainTextView.text = "로그인 하거나 회원가입 해주세요."
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.VISIBLE
                authNotEmail.visibility=View.VISIBLE
                authUsernameEditView.visibility = View.GONE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                authUsernameEditView.visibility=View.GONE
                signBtn.visibility = View.GONE
                hostSignBtn.visibility=View.GONE
                loginBtn.visibility = View.VISIBLE
                googleSignBtn.visibility=View.GONE
            }
        }else if(mode === "signin"){
            binding.run {
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.GONE
                authNotEmail.visibility=View.GONE
                authUsernameEditView.visibility = View.VISIBLE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                authUsernameEditView.visibility=View.VISIBLE
                signBtn.visibility = View.VISIBLE
                
                hostSignBtn.visibility=View.GONE
                loginBtn.visibility = View.GONE
                googleSignBtn.visibility=View.GONE
            }
        }
    }
}