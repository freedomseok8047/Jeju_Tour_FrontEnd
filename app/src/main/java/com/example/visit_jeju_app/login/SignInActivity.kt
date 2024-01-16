package com.example.visit_jeju_app.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.visit_jeju_app.MainActivity
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.R
import com.example.visit_jeju_app.databinding.ActivitySignInBinding
import com.example.visit_jeju_app.login.model.User
import com.example.visit_jeju_app.retrofit.addUserToMysql
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
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


        binding.loginBtn.setOnClickListener {
            //이메일, 비밀번호 로그인.......................
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(baseContext, "이메일과 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            MyApplication.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){ task ->
                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()
                    if(task.isSuccessful){
                        if(MyApplication.checkAuth()){
                            MyApplication.email = email
                            changeVisibility("login")
                            Toast.makeText(baseContext, "로그인 성공.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@SignInActivity, MainActivity::class.java)
                            intent.putExtra("USER_EMAIL", MyApplication.email)
                            startActivity(intent)
                            finish()
                        }else {
                            Toast.makeText(baseContext, "전송된 메일로 이메일 인증이 되지 않았습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }else {
                        Toast.makeText(baseContext, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    private fun signOut() {
        // [START auth_sign_out]
        Firebase.auth.signOut()
        // [END auth_sign_out]
    }

    //매개변수를 모드라는 변수명,문자열 타입.
    fun changeVisibility(mode: String){
        if(mode === "login"){
            binding.run {
                authMainTextView.text = "${MyApplication.email} 님 반갑습니다."

                //authNotEmail.visibility= View.GONE
                logoutBtn.visibility= View.VISIBLE
                //goSignInBtn.visibility= View.GONE

                //authUsernameEditView.visibility= View.GONE

                authEmailEditView.visibility= View.GONE
                authPasswordEditView.visibility= View.GONE
                //authUsernameEditView.visibility= View.GONE
                //signBtn.visibility= View.GONE
                //hostSignBtn.visibility= View.GONE
                loginBtn.visibility= View.GONE

                //googleSignBtn.visibility= View.GONE
            }

        }else if(mode === "logout"){
            binding.run {
                authMainTextView.text = "회원이라면 로그인 해 주세요."
                logoutBtn.visibility = View.GONE
                //goSignInBtn.visibility = View.VISIBLE
                //authNotEmail.visibility= View.VISIBLE
                //authUsernameEditView.visibility = View.GONE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                //authUsernameEditView.visibility= View.GONE
                //signBtn.visibility = View.GONE
                //hostSignBtn.visibility= View.GONE
                loginBtn.visibility = View.VISIBLE
                //googleSignBtn.visibility= View.GONE
            }
        }
        /*else if(mode === "signin"){
            binding.run {
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.GONE
                authNotEmail.visibility= View.GONE
                authUsernameEditView.visibility = View.VISIBLE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                authUsernameEditView.visibility= View.VISIBLE
                signBtn.visibility = View.VISIBLE

                hostSignBtn.visibility= View.GONE
                loginBtn.visibility = View.GONE
                googleSignBtn.visibility= View.GONE
            }
        }*/
    }
}