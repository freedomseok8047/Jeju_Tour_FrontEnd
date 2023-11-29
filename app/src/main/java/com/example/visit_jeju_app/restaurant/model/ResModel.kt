package com.example.visit_jeju_app.restaurant.model

import com.google.gson.annotations.SerializedName

//{
//    o1 "resId": 1,
//    11 "itemsAllTag": "애견미용실,반려동물,반려동물동반입장,혼저옵서개,반려동물공간_기타,공용주차장,화장실,무료 WIFI,유도 및 안내시설",
//    "itemsContentsid": "CNTS_300000000012715",
//    "itemsContentsCdValue": "c1",
//    o3 "itemsContentsCdLabel": "관광지",
//    "itemsContentsCdRefId": "contentscd>c1",
//    o2 "itemsTitle": "몽story",
//    "itemsRegion1CdValue": "region1",
//    o4 "itemsRegion1CdLabel": "제주시",
//    "itemsRegion1CdRefId": "region>region1",
//    o6 "itemsRegion2CdValue": 12,
//    o5 "itemsRegion2CdLabel": "애월",
//    "itemsRegion2CdRefId": "region1>12",
//    o7 "itemsAddress": "제주특별자치도 제주시 애월읍 하귀1리 157-6",
//    o8 "itemsRoadAddress": "제주특별자치도 제주시 애월읍 하귀9길 34",
//    "itemsTag": "애견미용실,반려동물,반려동물동반입장,혼저옵서개,반려동물공간_기타",
//    12 "itemsIntroduction": "몽story는 반려동물에게 최고의 서비스를 제공하는 장소다.",
//    o9 "itemsLatitude": 33.4840605,
//    10 "itemsLongitude": 126.4160275,
//    "itemsPostcode": null,
//    13 "itemsPhoneNo": "0507-1483-9982",
//    "itemsRepPhotoDescSeo": "몽story",
//    "itemsRepPhotoPhotoidPhotoid": 2020000000000,
//    14 "itemsRepPhotoPhotoidImgPath": "https://api.cdn.visitjeju.net/photomng/imgpath/202308/23/a42944ba-7801-41bc-bb67-6b48d7edd545.JPG",
//    15 "itemsRepPhotoPhotoidThumbnailPath": "https://api.cdn.visitjeju.net/photomng/thumbnailpath/202308/23/5ee3332e-148e-4f67-a2cb-de6754bd32b2.JPG"
//},
class ResModel{
    // o1 "resId": 1,
    var resId: Int? = null

    // o2 "itemsTitle": "몽story",
    var itemsTitle: String? = null

    // o3 "itemsContentsCdLabel": "관광지",
    var itemsContentsCdLabel: String? = null

    // o4 "itemsRegion1CdLabel": "제주시",
    var itemsRegion1CdLabel: String? = null

    // o5 "itemsRegion2CdLabel": "애월",
    var itemsRegion2CdLabel: String? = null

    // o6 "itemsRegion2CdValue": 12,
    var itemsRegion2CdValue: Int? = null

    // o7 "itemsAddress": "제주특별자치도 제주시 애월읍 하귀1리 157-6",
    var itemsAddress: String? = null

    // o8 "itemsRoadAddress": "제주특별자치도 제주시 애월읍 하귀9길 34",
    var itemsRoadAddress: String? = null

    // o9 "itemsLatitude": 33.4840605,
    var itemsLatitude: Double? = null

    // 10 "itemsLongitude": 126.4160275,
    var itemsLongitude: Double? = null

    // 11 "itemsAllTag": "애견미용실,반려동물,반려동물동반입장,혼저옵서개,반려동물공간_기타,공용주차장,화장실,무료 WIFI,유도 및 안내시설",
    var itemsAllTag: String? = null

    // 12 "itemsIntroduction": "몽story는 반려동물에게 최고의 서비스를 제공하는 장소다.",
    var itemsIntroduction: String? = null

    // 13 "itemsPhoneNo": "0507-1483-9982",
    var itemsPhoneNo: String? = null


    // 14 "itemsRepPhotoPhotoidImgPath": "https://api.cdn.visitjeju.net/photomng"
    var itemsRepPhotoPhotoidImgPath: String? = null

    // 15 "itemsRepPhotoPhotoidThumbnailPath": "https://api.cdn.visitjeju.net/photomng"
    var itemsRepPhotoPhotoidThumbnailPath: String? = null


}
