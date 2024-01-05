package com.example.visit_jeju_app.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.visit_jeju_app.MainActivity
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.MyApplication.Companion.auth
import com.example.visit_jeju_app.MyApplication.Companion.rdb
import com.example.visit_jeju_app.databinding.ActivityPhoneAuthBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class PhoneAuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityPhoneAuthBinding
    lateinit var phoneNum : String
    lateinit var authNum : String
    lateinit var messageAuthCode : String
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = Firebase.database.reference

        // 인증요청 버튼 클릭시
        binding.reqAuth.setOnClickListener {
            phoneNum = binding.inputPhone.text.toString()
            if (phoneNum.isBlank()) {
                Toast.makeText(this@PhoneAuthActivity, "전화번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val formattedPhoneNum = formatToInternational(phoneNum)
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(formattedPhoneNum)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }


        // 인증번호 확인 버튼 눌렀을 때
        binding.checkAuth.setOnClickListener {
            val userCode = binding.inputAuthNum.text.toString()
            val credential = PhoneAuthProvider.getCredential(messageAuthCode, userCode)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this@PhoneAuthActivity, SignUpActivity::class.java)
                        intent.putExtra("USER_EMAIL", MyApplication.email)
                        startActivity(intent)
                        Toast.makeText(this@PhoneAuthActivity, "인증이 확인 되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this@PhoneAuthActivity, "인증 코드를 다시 확인해주세요.", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.e("lsy", "인증 실패: ${task.exception}")
                        }
                    }
                }
        }
    }

    private fun formatToInternational(phoneNumber: String): String {
        // 대한민국 번호의 예시, 다른 국가 코드는 필요에 따라 조정
        return if (phoneNumber.startsWith("0")) {
            "+82" + phoneNumber.substring(1)
        } else {
            phoneNumber // 이미 국제 형식인 경우 그대로 반환
        }
    }

    // 콜백함수 설정 부분.
    var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("lsy", "인증이 자동으로 성공함!")
            finish()
        }
        // 인증 실패시 호출
        override fun onVerificationFailed(e: FirebaseException) {
            Log.d("로그인 과정 중 전화번호 인증", "인증 실패!")
            Log.d("로그인 과정 중 전화번호 인증", e.toString())

            // 인증 실패에 대한 적절한 사용자 피드백 제공
            if (e is FirebaseAuthInvalidCredentialsException) {
                // 전화번호가 유효하지 않은 경우의 처리
                Toast.makeText(this@PhoneAuthActivity, "전화번호가 유효하지 않습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseTooManyRequestsException) {
                // 너무 많은 요청이 발생한 경우의 처리
                Toast.makeText(this@PhoneAuthActivity, "요청이 너무 많습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                // Recaptcha 사용 시 관련된 액티비티가 누락된 경우의 처리
                Toast.makeText(this@PhoneAuthActivity, "내부 오류가 발생했습니다. 앱을 재시작해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                // 기타 오류에 대한 처리
                Toast.makeText(this@PhoneAuthActivity, "인증에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            Toast.makeText(this@PhoneAuthActivity, "인증 요청이 보내졌습니다.", Toast.LENGTH_SHORT).show()
            messageAuthCode = verificationId
        }
    }

}