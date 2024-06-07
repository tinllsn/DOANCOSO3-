package com.example.admin

data class User(
    var firstname: String = "",
    var lastname: String = "",
    var email: String = "",
    var uid: String = "",
    var imagePath: String = ""
) {
    // Secondary constructor for backward compatibility or specific use cases
    constructor() : this("", "", "", "", "")
}
