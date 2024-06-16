package com.example.doan.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize

//Parcelable là một giao diện trong Android dùng để tuần tự hóa (serialize) một lớp,
// làm cho nó có thể được truyền giữa các thành phần khác nhau của ứng dụng Android,
// chẳng hạn như giữa các activity hoặc fragment
data class Address(
    val addressTitle: String,
    val fullName: String,
    val street: String,
    val phone: String,
    val city: String,
    val state: String
): Parcelable {

    constructor(): this("","","","","","")
}

/*
*

data class Address(
    val addressTitle: String,
    val fullName: String,
    val street: String,
    val phone: String,
    val city: String,
    val state: String
) : Parcelable {

    // Constructor thứ cấp với các giá trị mặc định rỗng
    constructor() : this("", "", "", "", "", "")

    // Phương thức mô tả nội dung (thường không cần thay đổi, có thể trả về 0)
    override fun describeContents(): Int {
        return 0
    }

    // Phương thức ghi các thuộc tính vào Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(addressTitle)
        parcel.writeString(fullName)
        parcel.writeString(street)
        parcel.writeString(phone)
        parcel.writeString(city)
        parcel.writeString(state)
    }

    // Companion object chứa CREATOR để tạo lại đối tượng từ Parcel
    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: ""
            )
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }
}

*
* */