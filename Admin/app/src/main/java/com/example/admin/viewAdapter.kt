package com.example.admin

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class viewAdapter (val context: Context, val ds: List<Product> ) : RecyclerView.Adapter<viewAdapter.viewHolder> (){

   class viewHolder(itemview: View) : RecyclerView.ViewHolder(itemview){
        val image = itemview.findViewById<ImageView>(R.id.imageproduct)
        val name = itemview.findViewById<TextView>(R.id.txt_name)
        val price = itemview.findViewById<TextView>(R.id.txtprice)

  }

 override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
     val view:View = LayoutInflater.from(context).inflate(R.layout.test,parent,false)
     return viewHolder(view)
 }

 override fun getItemCount(): Int {
   return  ds.size
 }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
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

        holder.itemView.setOnClickListener {
            Log.wtf("15/06/2024",position.toString())
            Log.wtf("15/06/2024",product.id)
            val intent = Intent(context,UpdateProduct::class.java)
            intent.putExtra("id",product.id)
            context.startActivity(intent)
        }
    }



}