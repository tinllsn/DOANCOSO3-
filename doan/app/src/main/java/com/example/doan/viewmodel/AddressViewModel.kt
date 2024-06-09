package com.example.doan.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doan.data.Address
import com.example.doan.fragments.shopping.AddressFragmentArgs
import com.example.doan.util.Constants.USER_COLLECTION
import com.example.doan.util.Resource
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,

    ) : ViewModel() {
    //     hold and manage the state of adding a new address, allowing you to emit state updates
    private val _addNewAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    private val _deleteAddress = MutableStateFlow<Resource<Address>>(Resource.Unspecified())
    private lateinit var dbRef: DatabaseReference
    private val _error = MutableSharedFlow<String>()
    val addNewAddress = _addNewAddress.asStateFlow()
    val deleteAddress = _deleteAddress.asStateFlow()


    //    This is an extension function provided by Kotlin's coroutines library. It converts a MutableStateFlow into a read-only StateFlow.
    val error = _error.asSharedFlow()

    fun addAddress(address: Address) {
        val validateInputs = validateInputs(address)

        if (validateInputs) {
//            Trong coroutine, _addNewAddress.emit(Resource.Loading()) được gọi để đẩy một trạng thái "loading" vào MutableStateFlow _addNewAddress. Điều này thông báo cho các thành phần UI đã đăng ký về
            //            addNewAddress rằng một hoạt động đang được thực hiện và
            //            họ có thể hiển thị một giao diện người dùng phản hồi tương ứng (ví dụ: hiển thị thanh tiến trình).
            viewModelScope.launch { _addNewAddress.emit(Resource.Loading()) }
            firestore.collection("user").document(auth.uid!!).collection("address").document()
                .set(address).addOnSuccessListener {
//                     emit dung tao ra gia tri moi
                    viewModelScope.launch { _addNewAddress.emit(Resource.Success(address)) }
                }.addOnFailureListener {
                    viewModelScope.launch { _addNewAddress.emit(Resource.Error(it.message.toString())) }
                }
            dbRef = FirebaseDatabase.getInstance().getReference("user").child(auth.uid!!)
                .child("address")

            dbRef.child(address.addressTitle).setValue(address).addOnSuccessListener {
                Log.wtf("31_5_realtime", "success")
            }.addOnFailureListener {
                Log.wtf("31_5_realtime", "fail")

            }


        } else {
            viewModelScope.launch {
                _error.emit("All fields are required")
            }
        }
    }

    fun deleterAddress(address: Address) {
//        val validateInputs = validateInputs(address)
            var id = ""
//        if (validateInputs) {
        viewModelScope.launch { _addNewAddress.emit(Resource.Loading()) }

        dbRef = FirebaseDatabase.getInstance().getReference("user").child(auth.uid!!).child("address")

        dbRef.child(address.addressTitle).removeValue().addOnSuccessListener {
            val could = firestore.collection("user").document(auth.uid!!).collection("address")
            could.whereEqualTo("addressTitle", address.addressTitle).get()
                .addOnSuccessListener {
                    for (doc in it) {
                         id = doc.id
                        Log.wtf("test", id)

                        could.document(id)
                            .delete().addOnSuccessListener {
                                viewModelScope.launch { _deleteAddress.emit(Resource.Success(address)) }
                            }.addOnFailureListener {
                                viewModelScope.launch { _deleteAddress.emit(Resource.Error(it.message.toString())) }
                            }

                    }


                }
                .addOnFailureListener {
                    Log.e("Firestore", "Lỗi khi truy vấn tài liệu: ", it)
                }
//            firestore.collection("user").document(auth.uid!!).collection("address").document()
//                .delete().addOnSuccessListener {
//                    viewModelScope.launch { _deleteAddress.emit(Resource.Success(address)) }
//                }.addOnFailureListener {
//                    viewModelScope.launch { _deleteAddress.emit(Resource.Error(it.message.toString())) }
//                }
            Log.wtf("3_6_realtime", "success")
        }.addOnFailureListener {
            Log.wtf("3_6_realtime", "fail")

        }


//        }
//        else {
//            viewModelScope.launch {
//                _error.emit("All fields are required")
//            }
//        }
    }


    private fun validateInputs(address: Address): Boolean {
        return address.addressTitle.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty() &&
                address.state.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty() &&
                address.street.trim().isNotEmpty()
    }

}