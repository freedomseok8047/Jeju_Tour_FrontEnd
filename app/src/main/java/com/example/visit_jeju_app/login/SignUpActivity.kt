package com.example.visit_jeju_app.login

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.databinding.ActivitySignUpBinding
import com.example.visit_jeju_app.login.model.User
import com.example.visit_jeju_app.retrofit.addUserToMysql
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding :ActivitySignUpBinding

    // 이용약관 및 개인정보 취급방침 동의 프래그먼트
    private val Fragment_1 = 1
    private val Fragment_2 = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth= Firebase.auth

        binding.signBtn.setOnClickListener {
            // 이용약관 및 개인정보 처리방침 체크 여부 확인
            val isUserAgreementChecked = binding.checkboxUserAgreement.isChecked
            val isPrivacyPolicyChecked = binding.checkboxPrivacyPolicy.isChecked

            //이메일,비밀번호 회원가입
            val username = binding.authUsernameEditView.text.toString()
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()

            if (isUserAgreementChecked && isPrivacyPolicyChecked) {
                // 체크되어 있으면 회원가입 로직 실행
                val username = binding.authUsernameEditView.text.toString()
                val email = binding.authEmailEditView.text.toString()
                val password = binding.authPasswordEditView.text.toString()

                // Firebase 회원가입 로직
                MyApplication.auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Firebase 회원가입 성공 시 여기에 추가 작업을 수행할 수 있습니다.
                            // 예: 사용자 정보를 DB에 저장하거나 다른 후속 작업 실행 등
                            val userId = MyApplication.auth.currentUser?.uid ?: ""

                            // 이메일 인증 보내기
                            MyApplication.auth.currentUser?.sendEmailVerification()
                                ?.addOnCompleteListener { sendTask ->
                                    if (sendTask.isSuccessful) {
                                        // 이메일 인증 성공
                                        addUserToDatabase(username, email, auth.currentUser?.uid!!)
                                        addUserToMysql(username, email, userId)
                                        Log.d("lsy", "1차 확인")
                                        Toast.makeText(
                                            baseContext,
                                            "회원가입에서 성공, 전송된 메일을 확인해 주세요",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        changeVisibility("logout")
                                    } else {
                                        // 이메일 인증 실패
                                        Toast.makeText(baseContext, "메일 발송 실패", Toast.LENGTH_SHORT).show()
                                        changeVisibility("logout")
                                        Log.d("lsy", "1차 확인 실패")
                                    }
                                }
                            // 성공적으로 회원가입 완료 후 로그인 화면으로 이동하는 코드
                            val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Firebase 회원가입 실패
                            Toast.makeText(baseContext, "회원가입 실패", Toast.LENGTH_SHORT).show()
                            changeVisibility("logout")
                            Log.d("lsy", "1차 확인실패 2")
                        }
                    }
            } else {
                // 체크가 안 된 경우, 사용자에게 메시지 표시
                Toast.makeText(
                    baseContext,
                    "이용약관과 개인정보 처리방침에 동의해야 회원가입이 가능합니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // 이용약관 및 개인정보처리방침 동의
        //체크박스 클릭 시 이벤트 핸들러
        binding.checkboxUserAgreement.setOnCheckedChangeListener { buttonView, isChecked ->
            Toast.makeText(this@SignUpActivity, "이용약관 동의", Toast.LENGTH_SHORT).show()
        }
        binding.checkboxPrivacyPolicy.setOnCheckedChangeListener { buttonView, isChecked ->
            Toast.makeText(this@SignUpActivity, "개인정보 처리방침 동의", Toast.LENGTH_SHORT).show()
        }
        // 첫 번째 TextView에 밑줄 추가
        val btnUserAgreement = findViewById<TextView>(com.example.visit_jeju_app.R.id.btnUserAgreement)
        btnUserAgreement.paintFlags = btnUserAgreement.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        // 두 번째 TextView에 밑줄 추가
        val btnPrivacyPolicy = findViewById<TextView>(com.example.visit_jeju_app.R.id.btnPrivacyPolicy)
        btnPrivacyPolicy.paintFlags = btnPrivacyPolicy.paintFlags or Paint.UNDERLINE_TEXT_FLAG


    } //onCreate

    //이용약관, 개인정보취급방침 프래그먼트 호출
    fun openFragment(view: View) {
        val fragment = when (view.id) {
            com.example.visit_jeju_app.R.id.btnPrivacyPolicy -> PrivacyPolicyFragment()
            com.example.visit_jeju_app.R.id.btnUserAgreement -> UserAgreementFragment()
            else -> return
        }

        fragment.show(supportFragmentManager, "dialog_fragment_tag")

        // UserAgreementFragment의 CheckBox와 SignUpActivity의 CheckBox 동기화
        if (fragment is UserAgreementFragment) {
            fragment.setUserAgreementChangeListener(object : UserAgreementFragment.UserAgreementChangeListener {
                override fun onAgreementChanged(checked: Boolean) {
                    binding.checkboxUserAgreement.isChecked = checked
                }
            })
        } else if (fragment is PrivacyPolicyFragment) {
            fragment.setPrivacyPolicyListener(object  : PrivacyPolicyFragment.PrivacyPolicyListener {
                override fun onPrivacyPolicyChanged(checked: Boolean) {
                    binding.checkboxPrivacyPolicy.isChecked = checked
                }
            })
        }


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
        MyApplication.rdb.child("user").child(uId).setValue(User(name, email, uId))

    }

    //매개변수를 모드라는 변수명,문자열 타입.
    fun changeVisibility(mode: String){
        if(mode === "signin"){
            binding.run {
                //authMainTextView.text = "아직 회원이 아니라면 회원가입 해 주세요."
                //logoutBtn.visibility = View.GONE
                //goSignInBtn.visibility = View.GONE
                //authNotEmail.visibility= View.GONE
                authUsernameEditView.visibility = View.VISIBLE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                authUsernameEditView.visibility= View.VISIBLE
                signBtn.visibility = View.VISIBLE

                //hostSignBtn.visibility= View.GONE
                //loginBtn.visibility = View.GONE
                //googleSignBtn.visibility= View.GONE
            }
        }
    }
}

