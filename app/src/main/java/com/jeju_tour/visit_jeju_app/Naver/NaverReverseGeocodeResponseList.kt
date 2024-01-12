package com.camp.campingapp.model

import com.google.gson.annotations.SerializedName

data class NaverReverseGeocodeResponseList(
    @SerializedName("results")
    val results: List<ReverseGeocodeResult>
)


