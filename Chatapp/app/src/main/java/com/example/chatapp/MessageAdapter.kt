package com.example.chatapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList: ArrayList<Mesage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       if (viewType == 1) {
           // inflate receive
           val view :View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
           return ReceiveViewHolder(view)
       } else {
//            inflate sent
           val view :View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
           return SentViewHolder(view)
       }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageList[position]
//    neu la SentViewHolder
        if (holder.javaClass == SentViewHolder::class.java) {
//             do the stuff for sent view holder
            val viewHolder = holder as SentViewHolder
            holder.sendMessage.text = currentMessage.message
        } else {
//             do the stuff for receive view holder

            val viewHolder = holder as ReceiveViewHolder

            holder.receiveMessage.text = currentMessage.message
        }
    }

//     xác định tin nhắn gửi và tin nhắn nhận
    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]

        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
            return ITEM_SENT
        } else {
            return ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        val sendMessage = itemview.findViewById<TextView>(R.id.txt_sent_message)

    }

    class ReceiveViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val receiveMessage = itemview.findViewById<TextView>(R.id.txt_receive_message)

    }
}