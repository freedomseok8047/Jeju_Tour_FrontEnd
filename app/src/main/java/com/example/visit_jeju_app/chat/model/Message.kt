package com.example.visit_jeju_app.chat.model

data class Message(
    var message: String?,
    var sendId: String?
){
    constructor():this("","")
}
