package com.example.doan.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.doan.R
import com.example.doan.activities.ShoppingActivity
import com.example.doan.databinding.FragmentIntroductionBinding
import com.example.doan.viewmodel.IntroductionViewModel
import com.example.doan.viewmodel.IntroductionViewModel.Companion.ACCOUNT_OPTIONS_FRAGMENT
import com.example.doan.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class IntroductionFragment : Fragment(R.layout.fragment_introduction) {
    private lateinit var binding: FragmentIntroductionBinding
    private val viewModel by viewModels<IntroductionViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.navigate.collect {
                when (it) {
                    SHOPPING_ACTIVITY -> {

                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->

                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        }
                    }

                    ACCOUNT_OPTIONS_FRAGMENT -> {
                        findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)

                    }

                    else -> Unit
                }
            }
        }

        binding.buttonStart.setOnClickListener {
            viewModel.startButtonClcik()
            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
        }
    }
}