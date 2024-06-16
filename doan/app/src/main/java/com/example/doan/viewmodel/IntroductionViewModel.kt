package com.example.doan.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewModelScope
import com.example.doan.R
import com.example.doan.util.Constants.INTRODUCTION_KEY
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(
//    SharedPreferences: Được sử dụng để lưu trữ dữ liệu ứng dụng trong các tệp XML.
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth

) :ViewModel() {
// lưu trữ giá trị cho việc điều hướng
    private val _navigate = MutableStateFlow(0)
//    StateFlow cho phép các thành phần khác, như Fragment hoặc Activity,
//    quan sát giá trị của _navigate mà không thay đổi được giá trị của nó.
    val navigate: StateFlow<Int> = _navigate

    companion object {
//        SHOPPING_ACTIVITY: Một hằng số đại diện cho hoạt động mua sắm.
        const val SHOPPING_ACTIVITY = 23
        val ACCOUNT_OPTIONS_FRAGMENT = R.id.action_introductionFragment_to_accountOptionsFragment
    }
    init {
//         có chức năng kiểm tra xem người dùng đã nhấn nút bắt đầu trong lần sử dụng trước hay chưa.
        val isButtonClicked = sharedPreferences.getBoolean(INTRODUCTION_KEY, false)
        val user = firebaseAuth.currentUser

        if (user != null) {
            viewModelScope.launch {
//                emit() sẽ cập nhật giá trị mới cho _navigate
                _navigate.emit(SHOPPING_ACTIVITY)
            }
        } else if (isButtonClicked) {

            viewModelScope.launch {
                _navigate.emit(ACCOUNT_OPTIONS_FRAGMENT)
            }

        } else {
            Unit
        }
    }

    fun startButtonClcik() {
        sharedPreferences.edit().putBoolean(INTRODUCTION_KEY,true).apply()
    }
}