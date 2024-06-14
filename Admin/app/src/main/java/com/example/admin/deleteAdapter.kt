package com.example.admin

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase

import com.google.firebase.firestore.firestore

class deleteAdapter(val context: Context, val ds:  List<Product>) :
    RecyclerView.Adapter<deleteAdapter.deleteHolder>() {

    class deleteHolder (itemView:View) :RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.imageproduct)
        val name = itemView.findViewById<TextView>(R.id.txt_name)
        val price = itemView.findViewById<TextView>(R.id.txtprice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): deleteHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.test,parent,false)
        return deleteHolder(view)
    }

    override fun getItemCount(): Int {
        return ds.size
    }

    override fun onBindViewHolder(holder: deleteHolder, position: Int) {
        val product = ds[position]
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
            val id :String? = null
        holder.itemView.setOnClickListener {
                            Log.wtf("14/6/", position.toString())
            val fs  = Firebase.firestore.collection("Products")
            fs.whereEqualTo("id",product.id)
                .addSnapshotListener{snapshots, e ->
                    if (e != null) {
                        Log.wtf("14/6", "listen:error", e)
                        return@addSnapshotListener
                    }



                    for (document in snapshots!!.documents) {
                        Log.wtf("14/6/", document.id.toString())

                        fs.document(document.id).delete().addOnSuccessListener {
                            Log.wtf("14/6", "DocumentSnapshot successfully deleted!")

                            Toast.makeText(context," successfully deleted!",Toast.LENGTH_SHORT).show()
                        }
                            .addOnFailureListener { e -> Log.wtf("14/6", "Error deleting document", e) }

                    }
                }
        }
    }

}