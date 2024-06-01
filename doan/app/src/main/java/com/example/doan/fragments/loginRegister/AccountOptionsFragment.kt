package com.example.doan.fragments.loginRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.doan.R
import com.example.doan.databinding.FragmentAccountOptionsBinding
import com.example.doan.databinding.FragmentIntroductionBinding

class AccountOptionsFragment:Fragment(R.layout.fragment_account_options) {
    private lateinit var binding: FragmentAccountOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountOptionsBinding.inflate(inflater)
        return binding.root
    }
//  gọi sau khi fragment được tạo và liên kết với giao diện.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLoginAccountOptions.setOnClickListener {
            findNavController().navigate(R.id.action_accountOptionsFragment_to_login_Fragment)
        }
        binding.buttonRegisterAccountOptions.setOnClickListener {
            findNavController().navigate(R.id.action_accountOptionsFragment_to_registerFragement)
        }
    }
}