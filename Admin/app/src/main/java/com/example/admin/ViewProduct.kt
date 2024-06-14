package com.example.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.admin.databinding.ActivityViewProductBinding
import com.google.firebase.firestore.FirebaseFirestore

private lateinit var binding: ActivityViewProductBinding
class ViewProduct : AppCompatActivity() {

    private lateinit var productArray: ArrayList<Product>
    private lateinit var adapter : viewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productArray = ArrayList()

        adapter= viewAdapter(this@ViewProduct,productArray)

        binding.rvDelete.layoutManager = LinearLayoutManager(this)
        binding.rvDelete.adapter=adapter
        FirebaseFirestore.getInstance().collection("Products").addSnapshotListener { querySnapshot,e ->
            if (e != null) {
                Log.wtf("erorr", "Listen failed.", e)
                return@addSnapshotListener
            }

            productArray.clear()
            for (document in querySnapshot!!.documents) {
                Log.wtf("test", document.id)

                val product = document.toObject(Product::class.java)
                if (product != null) {
                    Log.wtf("test", product.toString())
                    // Thực hiện các hành động khác với đối tượng order nếu cần
                    productArray.add(product)
                }
            }
            adapter.notifyDataSetChanged()
        }

    }
}