package com.example.visit_jeju_app.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.visit_jeju_app.MyApplication
import com.example.visit_jeju_app.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // MyApplication.auth 초기화
        MyApplication.auth = MyApplication.auth

        if(MyApplication.checkAuth()){
            Log.d("hello", "로그인 인증 됨")
            changeVisi("login")
        } else {
            Log.d("hello", "로그인 인증 안됨")
            changeVisi("logout")
        }
        // 구글 로그인 기능 확인
        // 1)구글인증 버튼 클릭 -> 후처리 함수 호출
        // 2)후처리 함수 만들기 : 구글의 정보를 가지고 와서 처리
        val requestLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            // 실작업은 여기서 이루어짐
            // 구글인증 결과 처리
            // it.data : 구글로부터 받아온 계정정보
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            // 로그인 정보 유무 또는 네트워크 연결 오류 등으로 정보를 받거나 못 받을 수 있으므로 무조건 try, catch구문 사용
            try {
                // 계정정보 가져오기
                val account =  task.getResult(ApiException::class.java)
                // 계정의 정보 가져오기
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                MyApplication.auth.signInWithCredential(credential)
                    .addOnCompleteListener(this){
                            task ->
                        if (task.isSuccessful){
                            MyApplication.email = account.email
                            changeVisi("login")
                        } else {
                            changeVisi("logout")
                        }
                    }

            }catch (e: ApiException){
                changeVisi("logout")
            }
        }

        //구글 인증 버튼 클릭 시 해당 구글 계정 선택 화면으로 이동하는 인텐트 추가
        binding.googleAuthInBtn.setOnClickListener {
            // 샘플 코드
            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
                .requestEmail()
                .build()
            // 구글의 인증 화면으로 이동하는 코드
            val signInIntent = GoogleSignIn.getClient(this, gso).signInIntent
            // 후처리 함수 동작 연결
            requestLauncher.launch(signInIntent)
        }

        //로그아웃 버튼
        binding.logoutBtn.setOnClickListener {
            MyApplication.auth.signOut()
            MyApplication.email = null
            changeVisi("logout")
        }

        //이메일 비밀번호 기능 이용하기 : 파이어베이스 인증 기능임
        //실제로 인증 링크를 받을 수 있는 이메일로 테스트
        //여기서 사용하는 패스워드는 현재 로그인 하기 위한 패스워드

        binding.joinBtn.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTestPass.text.toString()

            MyApplication.auth.createUserWithEmailAndPassword(email, password)
                // 회원가입이 잘 되었을 경우, 호출되는 콜백함수
                .addOnCompleteListener(this){
                        task ->
                    binding.editTextEmail.text.clear()
                    binding.editTestPass.text.clear()
                    if(task.isSuccessful){
                        MyApplication.auth.currentUser?.sendEmailVerification()
                            //회원가입한 이메일에 인증 링크를 잘 보냈다면 수행하는 콜백함수
                            ?.addOnCompleteListener(this){
                                    sendTask ->
                                if (sendTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "회원가입 성공, 전송된 이메일을 확인해 주세요",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    changeVisi("logout")

                                    // 로그인 뷰 관련 수정1
                                } else {
                                    Toast.makeText(
                                        this, "메일 발송 실패",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    changeVisi("logout")
                                }
                            }
                    } else {
                        // 회원 가입 실패한 경우,
                        Toast.makeText(this, "회원가입 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        //가입한 이메일, 패스워드로 로그인
        binding.loginBtn.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTestPass.text.toString()

            MyApplication.auth.signInWithEmailAndPassword(email,password)
                //로그인이 잘 되었을 경우 실행될 콜백함수
                .addOnCompleteListener(this){
                        task->
                    binding.editTextEmail.text.clear()
                    binding.editTestPass.text.clear()
                    // 로그인이 된 경우
                    if (task.isSuccessful){
                        // 로그인이 되었을 때 인증확인
                        // 현재 유저 확인했을 경우
                        if (MyApplication.checkAuth()){
                            MyApplication.email = email
                            changeVisi("login")
                        } else {
                            Toast.makeText(this,"전송된 이메일로 인증이 안 되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this,"인증 실패", Toast.LENGTH_SHORT).show()
                    }

                }
        }



    }

    // 임의의 함수 만들기
    // 인증의 상태에 따라서 로그인 화면 표시 여부 : 로그인 되면 로그아웃 버튼 보이고, 로그인 안되면 로그아웃 버튼 그대로

    fun changeVisi(mode : String){
        if (mode === "login") {
            //로그인이 되었다면 인증된 이메일도 이미 등록되어서 가지고 옴
            binding.authMainText.text = "${MyApplication.email}님 반갑습니다."
            binding.logoutBtn.visibility = View.VISIBLE
            binding.loginBtn.visibility = View.GONE
            binding.googleAuthInBtn.visibility = View.GONE
            binding.joinBtn.visibility = View.GONE
            binding.signInBtn.visibility = View.GONE
            binding.editTextEmail.visibility = View.GONE
            binding.editTestPass.visibility = View.GONE
        }
        else if(mode === "logout") {
            binding.authMainText.text = "로그인 하거나 회원가입 하세요"
            binding.logoutBtn.visibility = View.GONE
            binding.loginBtn.visibility = View.VISIBLE
            binding.googleAuthInBtn.visibility = View.VISIBLE
            binding.joinBtn.visibility = View.VISIBLE
            binding.signInBtn.visibility = View.VISIBLE
            binding.editTextEmail.visibility = View.VISIBLE
            binding.editTestPass.visibility = View.VISIBLE
        }
        else if(mode === "signIn") {
            binding.logoutBtn.visibility = View.GONE
            binding.signInBtn.visibility = View.GONE
            binding.loginBtn.visibility = View.GONE
            binding.googleAuthInBtn.visibility = View.GONE
            binding.joinBtn.visibility = View.GONE
            binding.signInBtn.visibility = View.GONE
            binding.editTextEmail.visibility = View.VISIBLE
            binding.editTestPass.visibility = View.VISIBLE
        }
    }
}