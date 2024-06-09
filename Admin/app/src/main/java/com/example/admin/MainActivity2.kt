package com.example.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.admin.databinding.ActivityMain2Binding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

private lateinit var binding: ActivityMain2Binding
class MainActivity2 : AppCompatActivity() {
    private lateinit var userArray: ArrayList<User>
    private lateinit var adapter : main2Adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        userArray = ArrayList()

        adapter= main2Adapter(this@MainActivity2,userArray)
        binding.rvUser.layoutManager = LinearLayoutManager(this)
        binding.rvUser.adapter = adapter
        FirebaseFirestore.getInstance().collection("user").addSnapshotListener { querySnapshot, e ->
            if (e != null) {
                Log.wtf("erorr", "Listen failed.", e)
                return@addSnapshotListener
            }

            userArray.clear()
            for (document in querySnapshot!!.documents) {
                Log.wtf("test", document.id)

                val user = document.toObject(User::class.java)
                if (user != null) {
                    Log.wtf("test", user.toString())
                    // Thực hiện các hành động khác với đối tượng order nếu cần
                    userArray.add(user)
                }
            }
            adapter.notifyDataSetChanged()


        }
    }
}