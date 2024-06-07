package com.example.admin

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class HandleAdapter (var ds: List<Order>, val context: Context) : RecyclerView.Adapter<HandleAdapter.OrderViewHolder>() {


    class OrderViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
        val textstatus= itemView.findViewById<TextView>(R.id.txt_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {

        val view:View = LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false)
        return  OrderViewHolder(view)
    }

    override fun getItemCount(): Int {
            return ds.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder  = ds[position]
        var key = currentOrder.orderId.toString()

        holder.textName.text = currentOrder.orderId.toString()
        holder.textstatus.text = currentOrder.orderStatus

        holder.itemView.setOnClickListener {
                val intent = Intent(context,Orderdetail::class.java)
                intent.putExtra("id",key)
//                intent.putExtra("order",)
            Log.wtf("id",key)
            context.startActivity(intent)
        }
    }
}