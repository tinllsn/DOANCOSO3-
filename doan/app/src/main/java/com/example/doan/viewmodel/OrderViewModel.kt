package com.example.doan.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doan.data.order.Order
import com.example.doan.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private var dbRef: DatabaseReference
) : ViewModel() {

    private val _order = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val order = _order.asStateFlow()



    fun placeOrder(order: Order) {
        viewModelScope.launch {
            _order.emit(Resource.Loading())
        }
        firestore.runBatch { batch ->
            //TODO: Add the order into user-orders collection
            //TODO: Add the order into orders collection
            //TODO: Delete the products from user-cart collection

            firestore.collection("user")
                .document(auth.uid!!)
                .collection("orders")
                .document()
                .set(order)
//            save order into order of user in realtime
            dbRef = FirebaseDatabase.getInstance().getReference("user").child(auth.uid!!)
            dbRef.child("order").setValue(order).addOnSuccessListener {
                Log.wtf("04/06/2024","save")
            }.addOnFailureListener {
                Log.wtf("04/06/2024","not save")
            }
//            ===

            firestore.collection("orders").document().set(order)

 //             save order into orders  in realtime
            FirebaseDatabase.getInstance().getReference("orders").setValue(order)
                .addOnSuccessListener {
                    Log.wtf("04/06/2024","save2")
                }.addOnFailureListener {
                    Log.wtf("04/06/2024","not save2")
                }

            firestore.collection("user").document(auth.uid!!).collection("cart").get()
                .addOnSuccessListener {
                    it.documents.forEach {
                        it.reference.delete()
                    }
                }

            dbRef.child("cart").removeValue()
        }.addOnSuccessListener {
            viewModelScope.launch {
                _order.emit(Resource.Success(order))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _order.emit(Resource.Error(it.message.toString()))
            }
        }
    }

}















