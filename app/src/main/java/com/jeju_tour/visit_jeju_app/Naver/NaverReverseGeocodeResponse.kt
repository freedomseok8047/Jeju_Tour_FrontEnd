package com.camp.campingapp.model

import com.google.gson.annotations.SerializedName

data class NaverReverseGeocodeResponse(
    @SerializedName("results")
    val results: List<ReverseGeocodeResult>
)

data class ReverseGeocodeResult(
    @SerializedName("name")
    val name: String,
    @SerializedName("region")
    val region: ReverseGeocodeRegion
)

data class ReverseGeocodeRegion(
    @SerializedName("area1")
    val area1: ReverseGeocodeArea,
    @SerializedName("area2")
    val area2: ReverseGeocodeArea
    // 이하 원하는 만큼의 지역 정보 추가
)

data class ReverseGeocodeArea(
    @SerializedName("name")
    val name: String

    // 다른 정보들도 필요하면 추가
)

