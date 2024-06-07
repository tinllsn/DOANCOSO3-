package com.example.admin

import android.os.Parcelable

data class Address(
    val addressTitle: String,
    val fullName: String,
    val street: String,
    val phone: String,
    val city: String,
    val state: String
) {

    constructor(): this("","","","","","")
}
