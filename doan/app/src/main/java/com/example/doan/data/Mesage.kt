package com.example.doan.data

class Mesage {
    var message: String? = null
    var senderId: String? = null
    constructor(){}

    constructor(mesage: String?, senderId: String?) {
        this.message = mesage
        this.senderId = senderId
    }
}