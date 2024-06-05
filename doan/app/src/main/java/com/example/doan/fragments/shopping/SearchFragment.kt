package com.example.doan.fragments.shopping

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
import com.example.doan.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SearchFragment : Fragment(R.layout.fragment_chat) {
    private lateinit var binding: FragmentChatBinding
    private lateinit var messageList: ArrayList<Mesage>
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var mDbRef: DatabaseReference
    private lateinit var chatViewModel: ChatViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase components
        mDbRef = FirebaseDatabase.getInstance().reference
        val auth = FirebaseAuth.getInstance()

        // Initialize ViewModel manually
        chatViewModel = ChatViewModel(mDbRef, auth)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(requireContext(), messageList)

        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = messageAdapter
        }

        // Observe the message list LiveData
        chatViewModel.messageList.observe(viewLifecycleOwner, { updatedMessageList ->
            messageList.clear()
            messageList.addAll(updatedMessageList)
            messageAdapter.notifyDataSetChanged()
        })

        // Load the chat room messages
        chatViewModel.createRoom()

        binding.btnSend.setOnClickListener {
            val message = binding.edtMessageBox.text.toString().trim()
            if (message.isNotEmpty()) {
                chatViewModel.addMessage(message)
                binding.edtMessageBox.text.clear()
            }
        }
    }
}
