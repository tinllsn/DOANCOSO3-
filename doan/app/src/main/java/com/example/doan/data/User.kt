package com.example.doan.data

data class User(

    val firstName: String,
    val lastName: String,
    val email: String,
    var imagePath: String = "",
    val role: Int = 0
){
    constructor(): this("","","","")
}

