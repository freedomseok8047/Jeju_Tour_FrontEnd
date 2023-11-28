package com.example.visit_jeju_app.login.model

data class User(
    var username: String,
    var email: String,
    var uid: String
){

    constructor(): this("","","")
}
