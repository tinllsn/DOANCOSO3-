package com.example.doan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.doan.data.Mesage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatViewModel(
    private val mDbRef: DatabaseReference,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _messageList = MutableLiveData<ArrayList<Mesage>>()
//    messageList là một biến công cộng được khai báo là một LiveData chỉ đọc (val) được truy cập từ bên ngoài lớp ChatViewModel.
    val messageList: LiveData<ArrayList<Mesage>> get() = _messageList

    var receiverRoom: String? = null
    var senderRoom: String? = null
    val senderUid = auth.uid

    fun createRoom() {
        val receiverUid = "MRVwOJBpUPdxiKZF8Smag31Quc92"

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newMessageList = ArrayList<Mesage>()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Mesage::class.java)
                        if (message != null) {
                            newMessageList.add(message)
                        }
                    }
                    _messageList.value = newMessageList
                }

                override fun onCancelled(error: DatabaseError) {
                    // Log the error
                }
            })
    }

    fun addMessage(message: String) {
        val messageObject = Mesage(message, senderUid)
        mDbRef.child("chats").child(senderRoom!!).child("messages").push()
            .setValue(messageObject).addOnSuccessListener {
                mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                    .setValue(messageObject)
            }
    }
}
