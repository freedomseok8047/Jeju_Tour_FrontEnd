package com.example.visit_jeju_app.retrofit

import android.util.Log
import com.example.visit_jeju_app.accommodation.model.AccomList
import com.example.visit_jeju_app.festival.model.FesList
import com.example.visit_jeju_app.restaurant.model.ResList
import com.example.visit_jeju_app.shopping.model.ShopList
import com.example.visit_jeju_app.tour.model.TourList
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkServiceRegionNm {
    @GET("tour/tourList/{itemsRegion2CdValue}")
    fun getList(
        @Path("itemsRegion2CdValue") itemsRegion2CdValue: Int
    ): Call<List<TourList>>

    @GET("res/resList/{itemsRegion2CdValue}")
    fun getResList(
        @Path("itemsRegion2CdValue") itemsRegion2CdValue: Int
    ): Call<List<ResList>>

    @GET("accom/accomList/{itemsRegion2CdValue}")
    fun getAccomList(
        @Path("itemsRegion2CdValue") itemsRegion2CdValue: Int
    ): Call<List<AccomList>>

    @GET("shop/shopList/{itemsRegion2CdValue}")
    fun getShopList(
        @Path("itemsRegion2CdValue") itemsRegion2CdValue: Int
    ): Call<List<ShopList>>

    @GET("fes/fesList/{itemsRegion2CdValue}")
    fun getFesList(
        @Path("itemsRegion2CdValue") itemsRegion2CdValue: Int
    ): Call<List<FesList>>


    @GET("tour/tourAllList")
    fun GetTourList(): Call<List<TourList>>

    @GET("accom/accomAllList")
    fun GetAccomList(): Call<List<AccomList>>

    @GET("res/resAllList")
    fun GetResList(): Call<List<ResList>>

    @GET("shop/shopAllList")
    fun GetShopList(): Call<List<ShopList>>

    @GET("fes/fesAllList")
    fun GetFesList(): Call<List<FesList>>
//
//    @GET("fes/fesAllList")
//    fun GetFesList(): Call<List<FesList>>


    @GET("tour/tourList/tourByGPS")
    fun getTourGPS(
        @Query("lat") lat : Double?,
        @Query("lnt") lnt : Double?
    ): Call<List<TourList>>

//    http://10.100.104.32:8083/tour/tourList/tourByGPS?lat=33.4&lnt=126.2

    @GET("accom/accomList/accomByGPS")
    fun getAccomGPS(
        @Query("lat") lat : Double?,
        @Query("lnt") lnt : Double?
    ): Call<List<AccomList>>

    @GET("res/resList/resByGPS")
    fun getResGPS(
        @Query("lat") lat : Double?,
        @Query("lnt") lnt : Double?
    ): Call<List<ResList>>

    @GET("fes/fesList/fesByGPS")
    fun getFesGPS(
        @Query("lat") lat : Double?,
        @Query("lnt") lnt : Double?
    ): Call<List<FesList>>

    @GET("shop/shopList/shopByGPS")
    fun getShopGPS(
        @Query("lat") lat : Double?,
        @Query("lnt") lnt : Double?
    ): Call<List<ShopList>>


    @POST("users/register")
    fun registerUser(@Body userInfo: UserInfo): Call<ResponseBody>

}

// 사용자 정보 모델
data class UserInfo(
    val name: String,
    val email: String,
    val firebaseUid: String // 필드 이름과 타입 변경
)



fun addUserToMysql(name: String, email: String, firebaseUid: String) {
    // Retrofit을 사용하여 서버 API 호출
    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8083/") // 서버 URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userService = retrofit.create(NetworkServiceRegionNm::class.java)
    val userInfo = UserInfo(name, email, firebaseUid) // 수정된 UserInfo 객체 생성

    userService.registerUser(userInfo).enqueue(object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            if (response.isSuccessful) {
                Log.d("lsy", "MySQL에 사용자 정보 저장 성공")
            } else {
                Log.d("lsy", "MySQL에 사용자 정보 저장 실패 - 상태 코드: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Log.d("lsy", "서버 에러: ${t.message}")
        }
    })
}






