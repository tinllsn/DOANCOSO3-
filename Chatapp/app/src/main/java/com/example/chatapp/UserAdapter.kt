package com.example.chatapp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

//import com.google.firebase.auth.FirebaseAuth

class UserAdapter (var ds: List<Userget>, val context: Context) :RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private lateinit var mDbRef: DatabaseReference


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.user_layout,parent,false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
       return ds.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = ds[position]
        var key: String? = null
        holder.textName.text = currentUser.firstName

        holder.itemView.setOnClickListener {

            mDbRef = FirebaseDatabase.getInstance().getReference()
//            do bản chất bất đồng bộ (asynchronous) của Firebase addValueEventListener
            mDbRef .child("user").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (postSnapshot in snapshot.children) {

                        val user = postSnapshot.getValue(Userget::class.java)

                        if(currentUser.email== user?.email)    {

                             key = postSnapshot.key
                            Log.wtf("bang1",key)

                        }
                    }

                    if (key != null) {
                        // Dùng để gửi thành phần này sang thành phần khác
                        val intent = Intent(context, ChatActivity::class.java)
                        intent.putExtra("name", currentUser.firstName)
                        intent.putExtra("uid", key)
                        context.startActivity(intent)
                    } else {
                        Log.e("UserAdapter", "User key not found")
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

//     dùng để gửi thành phần này sang thành phần khác
//            intent.putExtra("name",currentUser.firstName)
            Log.wtf("key",key)
//            intent.putExtra("uid",key)
//
//            context.startActivity(intent)

        }
    }

    class UserViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
    }
}