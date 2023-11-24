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

//호스트 로그인
        if(MyApplication.checkAuth()){
            changeVisibility("h_login")
        }else {
            changeVisibility("logout")
        }

        //인텐트로 후처리하는 코드
        val requestLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult())
        {
            //구글 로그인 결과 처리...........................
            //it.data구글로 인증된 정보가 들어있음
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                //인증객체,인증수단(등록된 이메일,구글인증)
                MyApplication.auth.signInWithCredential(credential)
                    //구글 인증으로 성공 후 실행할 로직,
                    .addOnCompleteListener(this){ task ->
                        if(task.isSuccessful){

                            //구글인증으로 된 이메일의 현재앱의 로그인된 email 재할당하는 부분
                            MyApplication.email = account.email
                            //changeVisibility각모드마다 보여주는 뷰가 다름


                            changeVisibility("login")
                        }else {
                            changeVisibility("logout")
                        }
                    }
            }catch (e: ApiException){
                changeVisibility("logout")
            }
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
        binding.goHostSignBtn.setOnClickListener{
            changeVisibility("h_signin")
        }
        binding.goGoogleSignInBtn.setOnClickListener{
            changeVisibility("G_signin")
        }


//        binding.googleLoginBtn.setOnClickListener {
//            val gso = GoogleSignInOptions
//                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build()
//            val signInIntent = GoogleSignIn.getClient(this, gso).signInIntent
//            requestLauncher.launch(signInIntent)
//        }

//        binding.googleSignBtn.setOnClickListener {
//            val gso = GoogleSignInOptions
//                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build()
//            saveUser()
//            binding.authEmailEditView.text.clear()
//            binding.authPasswordEditView.text.clear()
//            binding.authUsernameEditView.text.clear()
//            binding.authAddressEditView.text.clear()
//            binding.authTelEditView.text.clear()
//            val signInIntent = GoogleSignIn.getClient(this, gso).signInIntent
//            requestLauncher.launch(signInIntent)
//        }

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
                    binding.authAddressEditView.text.clear()
                    binding.authTelEditView.text.clear()
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

        binding.hostSignBtn.setOnClickListener {
            //호스트이메일,비밀번호 회원가입........................
            val username = binding.authHostUsernameEditView.text.toString()
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()

            MyApplication.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){task ->
                    saveHost()
                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()
                    binding.authHostUsernameEditView.text.clear()
                    binding.authHostTelEditView.text.clear()
                    binding.authHostCampNameEditView.text.clear()
                    binding.authHostAddressEditView.text.clear()
                    if(task.isSuccessful){
                        MyApplication.auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener{ sendTask ->
                                if(sendTask.isSuccessful){
                                    Toast.makeText(baseContext, "host회원가입에서 성공, 전송된 메일을 확인해 주세요",
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
            //이메일, 비밀번호 로그인.......................
            val email = binding.authEmailEditView.text.toString()
            val password = binding.authPasswordEditView.text.toString()
            MyApplication.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this){ task ->
                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()
                    if(task.isSuccessful){
                        if(MyApplication.checkAuth()){
                            MyApplication.email = email
                            changeVisibility("login")
                            Toast.makeText(baseContext, "로그인 성공.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@AuthActivity, MainActivity::class.java)
                            startActivity(intent)
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





    private fun saveUser(){
        val data = mapOf(
            "email" to binding.authEmailEditView.text.toString(),
            "password" to binding.authPasswordEditView.text.toString(),
            "username" to binding.authUsernameEditView.text.toString(),
            "address" to binding.authAddressEditView.text.toString(),
            "tel" to binding.authTelEditView.text.toString()
        )
        MyApplication.db.collection("user")
            .add(data)
            .addOnFailureListener{
                Log.d("kkang", "data save error", it)
            }
    }
    private fun saveHost(){
        val data = mapOf(
            "email" to binding.authEmailEditView.text.toString(),
            "password" to binding.authPasswordEditView.text.toString(),
            "username" to binding.authHostUsernameEditView.text.toString(),
            "campname" to binding.authHostCampNameEditView.text.toString(),
            "addr" to binding.authHostAddressEditView.text.toString(),
            "tel" to binding.authHostTelEditView.text.toString()
        )
        MyApplication.db.collection("host")
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
                authSNS.visibility=View.GONE
                authNotEmail.visibility=View.GONE
                logoutBtn.visibility= View.VISIBLE
                goSignInBtn.visibility= View.GONE
                googleLoginBtn.visibility= View.GONE
                authUsernameEditView.visibility=View.GONE
                facebookLoginBtn.visibility=View.GONE
                authEmailEditView.visibility= View.GONE
                authPasswordEditView.visibility= View.GONE
                authAddressEditView.visibility=View.GONE
                authUsernameEditView.visibility=View.GONE
                authHostUsernameEditView.visibility=View.GONE
                authHostAddressEditView.visibility=View.GONE
                authHostCampNameEditView.visibility=View.GONE
                authTelEditView.visibility=View.GONE
                authHostTelEditView.visibility=View.GONE
                goHostSignBtn.visibility=View.GONE
                signBtn.visibility= View.GONE
                hostSignBtn.visibility=View.GONE
                loginBtn.visibility= View.GONE
                goGoogleSignInBtn.visibility=View.GONE
                googleSignBtn.visibility=View.GONE
            }

        }else if(mode === "logout"){
            binding.run {
                authMainTextView.text = "로그인 하거나 회원가입 해주세요."
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.VISIBLE
                googleLoginBtn.visibility = View.VISIBLE
                authSNS.visibility=View.VISIBLE
                authNotEmail.visibility=View.VISIBLE

                authUsernameEditView.visibility = View.GONE

                facebookLoginBtn.visibility=View.VISIBLE
                goHostSignBtn.visibility=View.VISIBLE

                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                authAddressEditView.visibility=View.GONE
                authUsernameEditView.visibility=View.GONE
                authHostUsernameEditView.visibility=View.GONE
                authTelEditView.visibility=View.GONE
                authHostTelEditView.visibility=View.GONE
                authHostAddressEditView.visibility=View.GONE
                authHostCampNameEditView.visibility=View.GONE
//                authTypeEditView.visibility = View.GONE

                signBtn.visibility = View.GONE
                goGoogleSignInBtn.visibility=View.VISIBLE
                hostSignBtn.visibility=View.GONE
                loginBtn.visibility = View.VISIBLE
                googleSignBtn.visibility=View.GONE
            }
        }else if(mode === "signin"){
            binding.run {
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.GONE
                googleLoginBtn.visibility = View.GONE
                authSNS.visibility=View.GONE
                authNotEmail.visibility=View.GONE

                authUsernameEditView.visibility = View.VISIBLE

                facebookLoginBtn.visibility=View.GONE
                goHostSignBtn.visibility=View.GONE

                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                authAddressEditView.visibility=View.VISIBLE
                authUsernameEditView.visibility=View.VISIBLE
                authHostUsernameEditView.visibility=View.GONE
                authTelEditView.visibility=View.VISIBLE
                authHostTelEditView.visibility=View.GONE
                authHostAddressEditView.visibility=View.GONE
                authHostCampNameEditView.visibility=View.GONE
//                authTypeEditView.visibility = View.VISIBLE
                signBtn.visibility = View.VISIBLE
                goGoogleSignInBtn.visibility=View.GONE
                hostSignBtn.visibility=View.GONE
                loginBtn.visibility = View.GONE
                googleSignBtn.visibility=View.GONE
            }
        }
        else if(mode==="h_signin"){
            binding.run{
                authSNS.visibility=View.GONE
                authNotEmail.visibility=View.GONE
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.GONE
                googleLoginBtn.visibility = View.GONE
                facebookLoginBtn.visibility=View.GONE
                goHostSignBtn.visibility=View.GONE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                authAddressEditView.visibility=View.GONE
                authUsernameEditView.visibility=View.GONE
                authHostUsernameEditView.visibility=View.VISIBLE
                authTelEditView.visibility=View.GONE
                authHostTelEditView.visibility=View.VISIBLE
                signBtn.visibility = View.GONE
                goGoogleSignInBtn.visibility=View.GONE
                hostSignBtn.visibility = View.VISIBLE
                loginBtn.visibility = View.GONE
                googleSignBtn.visibility=View.GONE
                authHostAddressEditView.visibility=View.VISIBLE
                authHostCampNameEditView.visibility=View.VISIBLE
            }
        }
        else if(mode==="G_signin"){
            binding.run{
                authSNS.visibility=View.GONE
                authNotEmail.visibility=View.GONE
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.GONE
                googleLoginBtn.visibility = View.GONE
                authUsernameEditView.visibility = View.VISIBLE
                facebookLoginBtn.visibility=View.GONE
                goHostSignBtn.visibility=View.GONE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                authAddressEditView.visibility=View.VISIBLE
                authUsernameEditView.visibility=View.VISIBLE
                authHostUsernameEditView.visibility=View.GONE
                authTelEditView.visibility=View.VISIBLE
                authHostTelEditView.visibility=View.GONE
                signBtn.visibility = View.GONE
                goGoogleSignInBtn.visibility=View.GONE
                hostSignBtn.visibility=View.GONE
                loginBtn.visibility = View.GONE
                googleSignBtn.visibility=View.VISIBLE
                authHostAddressEditView.visibility=View.GONE
                authHostCampNameEditView.visibility=View.GONE
            }
        }

    }
}