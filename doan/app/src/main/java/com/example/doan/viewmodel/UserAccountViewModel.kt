package com.example.doan.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.doan.KelineApplication
import com.example.doan.data.User
import com.example.doan.util.RegisterValidation
import com.example.doan.util.Resource
import com.example.doan.util.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.HashMap
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application
) : AndroidViewModel(app) {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }

        firestore.collection("user").document(auth.uid!!).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                user?.let {
                    viewModelScope.launch {
                        _user.emit(Resource.Success(it))
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _user.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun updateUser(user: User, imageUri: Uri?) {
        val areInputsValid = validateEmail(user.email) is RegisterValidation.Success
                && user.firstName.trim().isNotEmpty()
                && user.lastName.trim().isNotEmpty()

        if (!areInputsValid) {
            viewModelScope.launch {
                _user.emit(Resource.Error("Check your inputs"))
            }
            return
        }

        viewModelScope.launch {
            _updateInfo.emit(Resource.Loading())
        }

        if (imageUri == null) {
            saveUserInformation(user, true)

        } else {
            saveUserInformationWithNewImage(user, imageUri)
        }

        //        realtime
        val updates = hashMapOf<String, Any>(
            "firstName" to user.firstName,
            "lastName" to user.lastName

        )
        var dbRef = FirebaseDatabase.getInstance().getReference("user").child(auth.uid!!)
        dbRef.updateChildren(updates).addOnSuccessListener {
            Log.wtf("test","update profile")
        }.addOnFailureListener {
            Log.wtf("test","update profile not success")

        }




    }

    private fun saveUserInformationWithNewImage(user: User, imageUri: Uri) {
        viewModelScope.launch {
            try {
//                MediaStore.Images.Media.getBitmap(...) được sử dụng để lấy đối tượng Bitmap từ Uri của ảnh được chọn (imageUri),
   //                sử dụng MediaStore để truy cập vào ảnh từ bộ nhớ thiết bị.
                val imageBitmap = MediaStore.Images.Media.getBitmap(
                    getApplication<KelineApplication>().contentResolver,
                    imageUri
                )
//                Sau khi có được imageBitmap, nó được nén xuống dưới dạng mảng byte (ByteArray)
//                để tiết kiệm dung lượng và chuẩn bị cho việc lưu trữ.
                val byteArrayOutputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)
                val imageByteArray = byteArrayOutputStream.toByteArray()
//                Dòng val imageDirectory = storage.child("profileImages/${auth.uid}/${UUID.randomUUID()}") tạo một đường dẫn duy nhất cho
//                ảnh trong thư mục profileImages của người dùng.
                val imageDirectory =
                    storage.child("profileImages/${auth.uid}/${UUID.randomUUID()}")
//                Dòng val result = imageDirectory.putBytes(imageByteArray).await() thực hiện lưu trữ mảng byte của ảnh lên Firebase Storage bằng phương thức putBytes. Hàm await() được sử dụng để đợi cho đến khi hoạt động
//                lưu trữ hoàn thành trước khi tiếp tục thực hiện các lệnh tiếp theo.
                val result = imageDirectory.putBytes(imageByteArray).await()
//                Dòng val imageUrl = result.storage.downloadUrl.await().toString() sử dụng await() để lấy URL của ảnh sau khi đã được lưu trữ thành công trên Firebase Storage.
                val imageUrl = result.storage.downloadUrl.await().toString()
                saveUserInformation(user.copy(imagePath = imageUrl), false)
            } catch (e: Exception) {
                viewModelScope.launch {
                    _user.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

    private fun saveUserInformation(user: User, shouldRetrievedOldImage: Boolean) {
//        Bắt đầu một giao dịch. Trong giao dịch này, bạn có thể thực hiện nhiều thao tác đọc và ghi trên
        //        cơ sở dữ liệu Firestore mà đảm bảo tính nhất quán và nguyên tử.
        firestore.runTransaction { transaction ->
            val documentRef = firestore.collection("user").document(auth.uid!!)
            if (shouldRetrievedOldImage) {
                val currentUser = transaction.get(documentRef).toObject(User::class.java)
//                Hàm này được sử dụng để tạo một bản sao của đối tượng data class với một số thuộc tính được thay đổi.
                val newUser = user.copy(imagePath = currentUser?.imagePath ?: "")
//                transaction.set(documentRef, newUser): Cập nhật tài liệu người dùng với đối tượng newUser
                transaction.set(documentRef, newUser)
            } else {
                transaction.set(documentRef, user)
            }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _updateInfo.emit(Resource.Success(user))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _updateInfo.emit(Resource.Error(it.message.toString()))
            }
        }


    }

}












