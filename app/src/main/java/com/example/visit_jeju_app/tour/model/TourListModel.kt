package com.example.visit_jeju_app.tour.model

import com.google.gson.annotations.SerializedName

data class TourListModel(
    var tours: MutableList<TourModel>? = null
)
