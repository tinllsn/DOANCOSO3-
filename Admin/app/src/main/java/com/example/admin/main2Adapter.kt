package com.example.admin

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
import com.example.admin.databinding.ActivityMain2Binding


class main2Adapter (val context: Context,val ds: List<User> ) : RecyclerView.Adapter<main2Adapter. main2> (){

    class main2(itemview: View) : RecyclerView.ViewHolder(itemview){
//        val image = itemview.findViewById<ImageView>(R.id.imgUser)
        val name = itemview.findViewById<TextView>(R.id.txt_name)
        val email = itemview.findViewById<TextView>(R.id.txt_email)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  main2 {
        val view:View = LayoutInflater.from(context).inflate(R.layout.main2adapter,parent,false)
        return  main2(view)
    }

    override fun getItemCount(): Int {
        return  ds.size
    }

    override fun onBindViewHolder(holder:  main2, position: Int) {
        val user = ds[position]

        // Set product name
        holder.name.text = user.firstName + user.lastName
        holder.email.text = user.email
        // Load image using Glide if available
//        if (!user.imagePath.isNullOrEmpty()) {
//            Glide.with(context)
//                .load(user.imagePath[0]) // Load the first image
//                .into(holder.image)      // Specify the target ImageView
//        } else {
//            // Handle case when images are null or empty
//            Log.e("mainAdapter", "Product images are null or empty at position $position")
//            // You might want to set a placeholder image or do something else
//        }
    }



}