package com.example.doan.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.doan.data.User
import com.example.doan.util.Constants.USER_COLLECTION
import com.example.doan.util.RegisterFieldsState
import com.example.doan.util.RegisterValidation
import com.example.doan.util.Resource
import com.example.doan.util.validateEmail
import com.example.doan.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
//import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import java.security.PrivateKey
import javax.inject.Inject


@HiltViewModel

class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private var dbRef: DatabaseReference
) :
    ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register
//là một Channel để lưu trữ trạng thái kiểm tra các trường nhập liệu khi đăng ký.
//    Channel trong Kotlin là một khái niệm tương tự như một hàng đợi (queue) dùng để truyền tải các giá trị giữa các coroutine.
//    Nó là một cách an toàn và hiệu quả để giao tiếp giữa các coroutine.
    private val _validation = Channel<RegisterFieldsState>()

    val validation = _validation.receiveAsFlow()
//         tạo tài khoản
    fun createAccountWithEmailAndPassword(user: User, passwor: String) {

        if (checkValidation(user, passwor)) {
//            runBlocking được sử dụng ở đây để chờ coroutine hoàn thành trước khi tiếp tục.( hỗ trợ chạy đồng bộ cho coroutine)
            runBlocking {
                _register.emit(Resource.Loading())
            }
//             xac thuc trong authentication
            firebaseAuth.createUserWithEmailAndPassword(user.email, passwor).addOnSuccessListener {
                it.user?.let {
                    saveUserInfor(it.uid,user)
//                 chinh tu it sang firebaseAuth
//                    _register.value = Resource.Success(firebaseAuth)


                }
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
            //             xac thuc trong authentication
        } else {

            val registerFilesState = RegisterFieldsState(
//                 check
                validateEmail(user.email), validatePassword(passwor)

            )

            runBlocking {
                _validation.send(registerFilesState)
            }
        }

    }
//     luu tài khoản vào trong firestore
    private fun saveUserInfor(userUid: String, user: User) {
        db.collection(USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
//     luu vao realtime database
        dbRef = FirebaseDatabase.getInstance().getReference("user")
        dbRef.child(userUid).setValue(user)
            .addOnSuccessListener {
            Log.wtf("register","thanhcong")
        }.addOnFailureListener {
                Log.wtf("register","thatbai")

        }

    }
//     check the account
    private fun checkValidation(user: User, passwor: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(passwor)
        val shouldRegister = emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success
        return shouldRegister

    }
}