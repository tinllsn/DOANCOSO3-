package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private lateinit var binding: ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var messageList: ArrayList<Mesage>
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var mDbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var receiverRoom: String? = null
        var senderRoom: String? = null
//      dung để nhận thông tin từ putextra
        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        Log.wtf("uid",receiverUid)
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().getReference("chats")

        senderRoom = receiverUid + senderUid
        Log.wtf("testnul",senderRoom)
        receiverRoom = senderUid + receiverUid

        supportActionBar?.title = name

         messageList = ArrayList()
         messageAdapter = MessageAdapter(this, messageList)

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = messageAdapter

//        login for adding data to recyclerView
        Log.wtf("test2",senderRoom)
//         them tin nhan vao phong chat của recyclerview

        mDbRef.child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for (postSnapshot in snapshot.children) {
                       val mesage = postSnapshot.getValue(Mesage::class.java)
//                        Log.wtf("test3","chay")
                        messageList.add(mesage!!)
//                        Log.wtf("test",mesage.message)
                    }

                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatActivity", "Database error: ${error.message}")
                }
            })

//        adding the message
        binding.btnSend.setOnClickListener {

            val mesage = binding.edtMessageBox.text.toString()
            val messageObject = Mesage(mesage, senderUid)

            mDbRef.child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
            binding.edtMessageBox.setText("")
        }

    }
}