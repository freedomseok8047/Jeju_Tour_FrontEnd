package com.example.visit_jeju_app.accommodation.model

import com.google.gson.annotations.SerializedName

data class accomRegionNmList(
    @SerializedName("facltNm")
    var facltNm: String,
    @SerializedName("firstImageUrl")
    var firstImageUrl: String,
    @SerializedName("doNm")
    var doNm: String,
    @SerializedName("tel")
    var tel: String,
    @SerializedName("lineIntro")
    var lineIntro: String,
    @SerializedName("induty")
    var induty: String,
    @SerializedName("sbrsCl")
    var sbrsCl: String,
    @SerializedName("addr1")
    var addr1: String,
    @SerializedName("mapX")
    var mapX: Double,
    @SerializedName("mapY")
    var mapY: Double,
    @SerializedName("animalCmgCl")
    var animalCmgCl: String,
    @SerializedName("homepage")
    var homepage: String,
    @SerializedName("intro")
    var intro: String
)
