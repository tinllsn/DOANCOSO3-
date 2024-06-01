package com.example.doan.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.example.doan.firebase.FirebaseCommon
import com.example.doan.util.Constants.INTRODUCTION_SP


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// module Dagger-Hilt

@Module
// Xác định phạm vi mà module này được cài đặ
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
//    các phụ thuộc được cung cấp ở cấp độ toàn bộ ứng dụng
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestoreDatabase() = Firebase.firestore

    @Provides
    @Singleton
    fun provideDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference()
    }

    @Provides
    fun provideIntroductionSP(
        application: Application
    ) = application.getSharedPreferences(INTRODUCTION_SP, MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideFirebaseCommon(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ) = FirebaseCommon(firestore,firebaseAuth)

    @Provides
    @Singleton
    fun provideStorage() = FirebaseStorage.getInstance().reference

}