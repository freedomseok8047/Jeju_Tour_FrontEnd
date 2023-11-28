package com.example.visit_jeju_app.retrofit

import com.example.visit_jeju_app.accommodation.model.AccomList
import com.example.visit_jeju_app.restaurant.model.ResList
import com.example.visit_jeju_app.tour.model.TourList
import com.example.visit_jeju_app.tour.model.TourModel
import com.example.visit_jeju_app.tour.model.tourRegionNmList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkServiceRegionNm {
    @GET("tour/tourList/{itemsRegion2CdValue}")
    fun getList(
        @Path("itemsRegion2CdValue") itemsRegion2CdValue: Long
    ): Call<List<tourRegionNmList>>

//    @GET("res/resList/{itemsRegion2CdValue}")
//    fun getList(
//        @Path("itemsRegion2CdValue") itemsRegion2CdValue: Long
//    ): Call<List<resRegionNmList>>
//    @GET("accom/accomList/{itemsRegion2CdValue}")
//    fun getList(
//        @Path("itemsRegion2CdValue") itemsRegion2CdValue: Long
//    ): Call<List<accomRegionNmList>>


    @GET("tour/tourAllList")
    fun GetTourList(): Call<List<TourList>>

    @GET("accom/accomAllList")
    fun GetAccomList(): Call<List<AccomList>>

    @GET("res/resAllList")
    fun GetResList(): Call<List<ResList>>
//    @GET("res/resAllList")
//    fun GetResList(): Call<List<ResList>>
//
//    @GET("accom/accomAllList")
//    fun GetAccomList(): Call<List<AccomList>>
//
//    @GET("shop/shopAllList")
//    fun GetShopList(): Call<List<ShopList>>
//
//    @GET("fes/fesAllList")
//    fun GetFesList(): Call<List<FesList>>


    @GET("tour/{lat}/{lnt}")
    fun GetTourGPS(
        @Query("lat") lat : Double?,
        @Query("lnt") lnt : Double?
    ): Call<List<TourList>>




}






