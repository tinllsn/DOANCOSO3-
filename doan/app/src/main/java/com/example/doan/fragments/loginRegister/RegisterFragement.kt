package com.example.doan.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.doan.R
import com.example.doan.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

//import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.doan.activities.ShoppingActivity
import com.example.doan.data.User
import com.example.doan.util.RegisterValidation
import com.example.doan.util.Resource
import com.example.doan.viewmodel.RegisterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val TAG = "RegisterFragment"

@AndroidEntryPoint
class RegisterFragement : Fragment(R.layout.fragment_register) {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
// điều hướng
        binding.buttonRegisterRegister.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragement_to_login_Fragment)
        }

        binding.apply {
            buttonRegisterRegister.setOnClickListener {
                val user = User(
                    edFirstNameRegister.text.toString().trim(),
                    edLastNameRegister.text.toString().trim(),
                    edEmailRegister.text.toString().trim()
                )
                val password = edPasswordRegister.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonRegisterRegister.startAnimation()
                    }

                    is Resource.Success -> {
                        Log.d("test", it.data.toString())
                        binding.buttonRegisterRegister.revertAnimation()
//   Hàm mở rộng also được sử dụng để thực hiện một hành động trên đối tượng Intent trước khi trả về nó. Trong trường hợp này,
//   hàm này được sử dụng để thêm các cấu hình khác cho Intent trước khi nó được sử dụng để khởi chạy một Activity mới.
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        }

                    }

                    is Resource.Error -> {
                        Log.e(TAG, it.message.toString())
                        binding.buttonRegisterRegister.revertAnimation()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect { validation ->
                if (validation.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edEmailRegister.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if (validation.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edPasswordRegister.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }


        }
        Log.wtf("registerfragment","error2")

    }
}