package com.example.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.databinding.ActivityHandleOrderBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

private lateinit var binding: ActivityHandleOrderBinding
class HandleOrder : AppCompatActivity() {

    private lateinit var rv : RecyclerView
    private lateinit var orderArray: ArrayList<Order>
    private lateinit var adapter : HandleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHandleOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderArray = ArrayList()
        adapter = HandleAdapter(orderArray,this)

        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        binding.rvOrders.adapter = adapter
        val db = FirebaseFirestore.getInstance().collection("orders").get().addOnSuccessListener { querySnapshot ->
            orderArray.clear()
            for (document in querySnapshot.documents) {
                val order = document.toObject(Order::class.java)
                if (order != null) {
                    Log.wtf("test", order.orderId.toString())
                    // Thực hiện các hành động khác với đối tượng order nếu cần
                    orderArray.add(order)

                }
            }
            adapter.notifyDataSetChanged()

        }.addOnFailureListener { exception ->
            Log.wtf("test", "Error getting documents: ", exception)
        }



    }




}