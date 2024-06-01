package com.example.doan.data

// giới hạn lớp con
sealed class Category(val category: String) {

    object Chair: Category("Chair")
    object Cupboard: Category("Cupboard")
    object Table: Category("Table")
    object Accessory: Category("Accessory")
    object Furniture: Category("Furniture")
}