package com.example.doan.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
//import kotlinx.parcelize.Parcelize
/*Parcelable là một giao diện (interface) trong Android, được thiết kế để giúp việc truyền
dữ liệu qua các Intent, Bundle, hoặc giữa các thành phần như hoạt động (Activity) và dịch vụ (Service)*/
@Parcelize
data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Float,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val colors: List<Int>? = null,
    val sizes: List<String>? = null,
    val images: List<String>
)
    : Parcelable
{
    constructor():this("0","","",0f,images = emptyList())

}