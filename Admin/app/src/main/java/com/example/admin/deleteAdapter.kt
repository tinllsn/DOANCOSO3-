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
import com.bumptech.glide.Glide
import com.example.admin.databinding.ActivityDeleteProductBinding


class deleteAdapter (val context: Context,val ds: List<Product> ) : RecyclerView.Adapter<deleteAdapter.deleteHolder> (){

   class deleteHolder(itemview: View) : RecyclerView.ViewHolder(itemview){
        val image = itemview.findViewById<ImageView>(R.id.imageproduct)
        val name = itemview.findViewById<TextView>(R.id.txt_name)
        val price = itemview.findViewById<TextView>(R.id.txtprice)

  }

 override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): deleteHolder {
     val view:View = LayoutInflater.from(context).inflate(R.layout.test,parent,false)
     return deleteHolder(view)
 }

 override fun getItemCount(): Int {
   return  ds.size
 }

    override fun onBindViewHolder(holder: deleteHolder, position: Int) {
        val product = ds[position]

        // Set product name
        holder.name.text = product.name
        product.offerPercentage?.let {
            val remainingPricePercentage = 1f - it
            val priceAfterOffer = remainingPricePercentage * product.price
            holder.price.text = "$ ${String.format("%.2f",priceAfterOffer)}"
        }

        // Load image using Glide if available
        if (!product.images.isNullOrEmpty()) {
            Glide.with(context)
                .load(product.images[0]) // Load the first image
                .into(holder.image)      // Specify the target ImageView
        } else {
            // Handle case when images are null or empty
            Log.e("deleteAdapter", "Product images are null or empty at position $position")
            // You might want to set a placeholder image or do something else
        }
    }



}