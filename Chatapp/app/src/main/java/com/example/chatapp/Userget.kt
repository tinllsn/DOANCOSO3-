package com.example.chatapp

data class Userget(


    var email: String = "",
    var firstName: String = "",
    var imagePath: String = "",
    var lastName: String = "",
){
    constructor() : this("", "", "", "")

}
