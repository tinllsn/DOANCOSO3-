package com.example.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.admin.databinding.ActivityOrderdetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

private lateinit var binding: ActivityOrderdetailBinding
class Orderdetail : AppCompatActivity() {
    private lateinit var mDbRef: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderdetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mDbRef = FirebaseFirestore.getInstance()

        val corderId = intent.getStringExtra("id")
        if (corderId != null) {
            Log.wtf("Received ID", corderId)

            mDbRef.collection("orders").addSnapshotListener { value, error ->
                if (error != null) {
                    // Xử lý lỗi nếu có
                    Log.e("TAG", "Listen failed.", error)
                    return@addSnapshotListener
                }

                for (document in value!!) {
                    val order = document.toObject(Order::class.java)
                    if (order != null && order.orderId == corderId.toLong()) {
                        Log.wtf("orderchay", corderId)

                        binding.apply {
                            textView2.text = order.orderId.toString()
                            textView3.text = order.orderStatus
                            textView4.text = order.date
                            textView5.text = order.totalPrice.toString()
                        }
                    }
                }
            }


//            mDbRef.collection("orders").get()
//                .addOnSuccessListener { querySnapshot ->
//                    for (document in querySnapshot.documents) {
//                        val order = document.toObject(Order::class.java)
//                        if (order != null) {
//                            Log.wtf("wtf", order.toString())
//                            if (order.orderId == corderId.toLong()) {
//                                Log.wtf("orderchay", corderId)
//
//                                binding.apply {
//                                textView2.text = order.orderId.toString()
//                                textView3.text = order.orderStatus
//                                textView4.text = order.date
//                                textView5.text = order.totalPrice.toString()
//                            }
//
//                            }
//                        }
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    // Xử lý lỗi nếu có
//                    Log.e("TAG", "Error getting documents: ", exception)
//                }

            binding.apply {
                btnConfirmed.setOnClickListener {

                    mDbRef.collection("orders").addSnapshotListener { value, error ->
                        if (error != null) {
                            // Xử lý lỗi nếu có
                            Log.e("TAG", "Listen failed.", error)
                            return@addSnapshotListener
                        }

                        for (document in value!!) {
                            val order = document.toObject(Order::class.java)
                            if (order != null && order.orderId == corderId.toLong()) {
                                Log.wtf("07/6/2024", corderId)
                                    document.reference.update("orderStatus", "Confirmed")
                                        .addOnSuccessListener {
                                            Log.wtf("07/6/2024", "DocumentSnapshot successfully updated!")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("07/6/2024", "Error updating document", e)
                                        }
                            }
                        }
                    }

                    mDbRef.collection("orders").addSnapshotListener { value, error ->
                        if (error != null) {
                            // Xử lý lỗi nếu có
                            Log.e("TAG", "Listen failed.", error)
                            return@addSnapshotListener
                        }

                        for (document in value!!) {
                            val order = document.toObject(Order::class.java)
                            if (order != null && order.orderId == corderId.toLong()) {
                                Log.wtf("07/6/2024", corderId)
                                document.reference.update("orderStatus", "Confirmed")
                                    .addOnSuccessListener {
                                        Log.wtf("07/6/2024", "DocumentSnapshot successfully updated!")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("07/6/2024", "Error updating document", e)
                                    }
                            }
                        }
                    }

//                    updateOrder()
                }
                btnCanceled.setOnClickListener {

                    mDbRef.collection("orders").addSnapshotListener { value, error ->
                        if (error != null) {
                            // Xử lý lỗi nếu có
                            Log.e("TAG", "Listen failed.", error)
                            return@addSnapshotListener
                        }

                        for (document in value!!) {
                            val order = document.toObject(Order::class.java)
                            if (order != null && order.orderId == corderId.toLong()) {
                                Log.wtf("07/6/2024", corderId)
                                document.reference.update("orderStatus", "Canceled")
                                    .addOnSuccessListener {
                                        Log.wtf("07/6/2024", "DocumentSnapshot successfully updated!")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("07/6/2024", "Error updating document", e)
                                    }
                            }
                        }
                    }
                }

                btnDelivered.setOnClickListener {
                    mDbRef.collection("orders").addSnapshotListener { value, error ->
                        if (error != null) {
                            // Xử lý lỗi nếu có
                            Log.e("TAG", "Listen failed.", error)
                            return@addSnapshotListener
                        }

                        for (document in value!!) {
                            val order = document.toObject(Order::class.java)
                            if (order != null && order.orderId == corderId.toLong()) {
                                Log.wtf("07/6/2024", corderId)
                                document.reference.update("orderStatus", "Delivered")
                                    .addOnSuccessListener {
                                        Log.wtf("07/6/2024", "DocumentSnapshot successfully updated!")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("07/6/2024", "Error updating document", e)
                                    }
                            }
                        }
                    }
                }

                btnShipped.setOnClickListener {
                    mDbRef.collection("orders").addSnapshotListener { value, error ->
                        if (error != null) {
                            // Xử lý lỗi nếu có
                            Log.e("TAG", "Listen failed.", error)
                            return@addSnapshotListener
                        }

                        for (document in value!!) {
                            val order = document.toObject(Order::class.java)
                            if (order != null && order.orderId == corderId.toLong()) {
                                Log.wtf("07/6/2024", corderId)
                                document.reference.update("orderStatus", "Shipped")
                                    .addOnSuccessListener {
                                        Log.wtf("07/6/2024", "DocumentSnapshot successfully updated!")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("07/6/2024", "Error updating document", e)
                                    }
                            }
                        }
                    }
                }
            }

        } else {
            Log.wtf("Error", "Received null ID")
        }


    }

//    private fun updateOrder() {
//        val db = FirebaseFirestore.getInstance()
//
//        db.collectionGroup("orders")
//            .get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    Log.wtf("07/6/2024", document.toString())
//
//                    Log.wtf("07/6/2024", document.data.toString())
//
////                    val orderIDs = mutableListOf<String>()
//
//
////                        if (document.get("key")) {
//////                            val orderID = document.getLong("orderId")
////                            Log.wtf("07/6/2024", "====")
////
////                            Log.wtf("07/6/2024", document.toString())
//////                            Log.wtf("07/6/2024", orderID.toString())
////
////                        }
//
//
//
//                }
//            }
//            .addOnFailureListener { exception ->
//                // Xử lý lỗi nếu có
//                Log.e("TAG", "Error getting documents", exception)
//            }
//
//
//
//    }
}