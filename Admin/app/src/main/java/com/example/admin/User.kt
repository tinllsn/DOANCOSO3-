package com.example.admin

data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    var imagePath: String = "",
    val role: Int = 1
){
    constructor(): this("","","","")
}