package com.example.visit_jeju_app

import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MyApplication : MultiDexApplication() {
    companion object {
        // 자바 :
        // 코틀린 :
        //static과 비슷, 해당 클래스 명으로 멤버에 접근 가능
        //인증기능에 접근하는 인스턴스가 필요
        lateinit var  auth : FirebaseAuth
        //인증할 이메일
        var email : String? = null

        // 이미지 저장소 , 인스턴스 도구
        lateinit var storage: FirebaseStorage
        // 파이어 스토어, 인스턴스 도구
        lateinit var db : FirebaseFirestore


        //MyApplication.checkAuth() : 이렇게 클래스명. 함수 및 특정 변수에 접근 가능
        fun checkAuth(): Boolean{
            var currentUser = auth.currentUser
            return currentUser?.let {
                email = currentUser.email
                currentUser.isEmailVerified
            } ?: let {
                false
            }
        }
    } //companion


    // 생명주기 최초 1회 동작
    override fun onCreate() {
        super.onCreate()
        //초기화
        auth = Firebase.auth
        storage = Firebase.storage
        db = FirebaseFirestore.getInstance()
    }
}