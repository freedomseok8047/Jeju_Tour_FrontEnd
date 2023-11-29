package com.example.visit_jeju_app

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.example.visit_jeju_app.retrofit.NetworkServiceRegionNm
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApplication : MultiDexApplication() {
    companion object {
        // 자바 :
        // 코틀린 :
        //static과 비슷, 해당 클래스 명으로 멤버에 접근 가능
        //인증기능에 접근하는 인스턴스가 필요
        lateinit var  auth : FirebaseAuth
        lateinit var rdb: DatabaseReference
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

        // 현재 위치 담아 두는 변수
//        var lat : Double = 0.0
//        var lnt : Double = 0.0

//        @SuppressLint("MissingPermission")
//        fun getLocation(context: Context) {
//            val fusedLocationProviderClient =
//                LocationServices.getFusedLocationProviderClient(context)
//
//            fusedLocationProviderClient.lastLocation
//                .addOnSuccessListener { success: Location? ->
//                    success?.let { location ->
//                        Log.d("lsy", "현재 위치 조회 : lat : ${location.latitude}, lnt : ${location.longitude}")
//                        lat = location.latitude
//                        lnt = location.longitude
//                        Log.d("lsy", "현재 위치 조회 2 : lat : ${lat}, lnt : ${lnt}")
//                    }
//                }
//                .addOnFailureListener { fail ->
//                    Log.d("lsy", "현재 위치 조회 실패")
//                }
//        }


    } //companion


    // 생명주기 최초 1회 동작
    override fun onCreate() {
        super.onCreate()
        //초기화
        auth = Firebase.auth
        storage = Firebase.storage
        db = FirebaseFirestore.getInstance()
        rdb = Firebase.database.reference
    }
    val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl("http://10.100.104.32:8083/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    var networkService = retrofit.create(NetworkServiceRegionNm::class.java)

}

val naver: Retrofit
    get() = Retrofit.Builder()
        .baseUrl("https://naveropenapi.apigw.ntruss.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

var networkService: NetworkServiceRegionNm = naver.create(NetworkServiceRegionNm::class.java)