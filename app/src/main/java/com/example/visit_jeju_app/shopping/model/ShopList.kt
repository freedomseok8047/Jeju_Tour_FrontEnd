package com.example.visit_jeju_app.shopping.model

import com.google.gson.annotations.SerializedName

data class ShopList(
    // o1 "shopId": 1,
    @SerializedName("shopId")
    var shopId: Long? = null,

    // o2 "itemsTitle": "몽story",
    @SerializedName("itemsTitle")
    var itemsTitle: String? = null,

    // o3 "itemsContentsCdLabel": "관광지",
    @SerializedName("itemsContentsCdLabel")
    var itemsContentsCdLabel: String? = null,

    // o4 "itemsRegion1CdLabel": "제주시",
    @SerializedName("itemsRegion1CdLabel")
    var itemsRegion1CdLabel: String? = null,

    // o5 "itemsRegion2CdLabel": "애월",
    @SerializedName("itemsRegion2CdLabel")
    var itemsRegion2CdLabel: String? = null,

    // o6 "itemsRegion2CdValue": 12,
    @SerializedName("itemsRegion2CdValue")
    var itemsRegion2CdValue: Int? = null,

    // o7 "itemsAddress": "제주특별자치도 제주시 애월읍 하귀1리 157-6",
    @SerializedName("itemsAddress")
    var itemsAddress: String? = null,

    // o8 "itemsRoadAddress": "제주특별자치도 제주시 애월읍 하귀9길 34",
    @SerializedName("itemsRoadAddress")
    var itemsRoadAddress: String? = null,

    // o9 "itemsLatitude": 33.4840605,
    @SerializedName("itemsLatitude")
    var itemsLatitude: Double,

    // 10 "itemsLongitude": 126.4160275,
    @SerializedName("itemsLongitude")
    var itemsLongitude: Double,

    // 11 "itemsAllTag": "애견미용실,반려동물,반려동물동반입장,혼저옵서개,반려동물공간_기타,공용주차장,화장실,무료 WIFI,유도 및 안내시설",
    @SerializedName("itemsAllTag")
    var itemsAllTag: String? = null,

    // 12 "itemsIntroduction": "몽story는 반려동물에게 최고의 서비스를 제공하는 장소다.",
    @SerializedName("itemsIntroduction")
    var itemsIntroduction: String? = null,

    // 13 "itemsPhoneNo": "0507-1483-9982",
    @SerializedName("itemsPhoneNo")
    var itemsPhoneNo: String? = null,


    // 14 "itemsRepPhotoPhotoidImgPath": "https://api.cdn.visitjeju.net/photomng"
    @SerializedName("itemsRepPhotoPhotoidImgPath")
    var itemsRepPhotoPhotoidImgPath: String? = null,

    // 15 "itemsRepPhotoPhotoidThumbnailPath": "https://api.cdn.visitjeju.net/photomng"
    @SerializedName("itemsRepPhotoPhotoidThumbnailPath")
    var itemsRepPhotoPhotoidThumbnailPath: String? = null


)
