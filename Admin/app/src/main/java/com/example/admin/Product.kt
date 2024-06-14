package com.example.admin

import android.net.Uri

data class Product(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val price: Float = 0f,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val colors: List<String>? = null,
    val sizes: List<String>? = null,
    val images: List<String>?= null

)