package com.example.doan.fragments.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doan.R
import com.example.doan.adapter.MessageAdapter
import com.example.doan.data.Mesage
import com.example.doan.databinding.FragmentChatBinding
import com.example.doan.viewmodel.BillingViewModel
import com.example.doan.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
//class chatFragment:Fragment(R.layout.fragment_chat) {
//    private lateinit var binding: FragmentChatBinding
//    private lateinit var messageList: ArrayList<Mesage>
//    private lateinit var messageAdapter: MessageAdapter
//    private lateinit var mDbRef: DatabaseReference
//    private val chatViewModel by viewModels<ChatViewModel>()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentChatBinding.inflate(inflater, container, false)
//        val view = binding.root
//
//        return view
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        Log.wtf("adapter","chay")
//        messageList = ArrayList()
//        messageAdapter = MessageAdapter(requireContext(), messageList)
//        Log.wtf("viewModel",requireContext().toString())
//        Log.wtf("adapter","chay")
//
//
//
//        binding.apply {
//            chatRecyclerView.adapter = messageAdapter
//            chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//
//
//        }
//
//        createRoom(messageList)
//
//        binding.apply {
//            val mesage = edtMessageBox.text.toString()
//            btnSend.setOnClickListener {
//                addMessage(mesage)
//            }
//        }
//
//
//
//    }
//
//    private fun createRoom(messageList: ArrayList<Mesage>) {
//        chatViewModel.createRoom(messageList)
//    }
//
//    private fun addMessage(message: String){
//        chatViewModel.addMessage(message)
//    }
//}