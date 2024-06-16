package com.example.doan.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.doan.R
import com.example.doan.databinding.ActivityShoppingBinding
import com.example.doan.util.Resource
import com.example.doan.viewmodel.CartViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

//     databiding
    val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }
//     tao ra doi tuong view model
    val viewModel by viewModels<CartViewModel>()
// hàm khoi tao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//     thiet lâp layout
        setContentView(binding.root)

        val navController = findNavController(R.id.shoppingHostFragment)
        binding.bottomNavigation.setupWithNavController(navController)

//     coroutine

        lifecycleScope.launchWhenStarted {
//         viewModel.cartProducts là một Flow trong ViewModel. Flow là một thành phần của thư viện Kotlin
  //         Coroutines, được sử dụng để xử lý các luồng dữ liệu không đồng bộ.
//            collectLatest là một hàm của Flow, nó thu thập các giá trị mới nhất từ Flow

            viewModel.cartProducts.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        val count = it.data?.size ?: 0
                        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
//                        bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply { ... }
//                        tạo hoặc lấy một badge (huy hiệu) cho cartFragment và áp dụng các thuộc tính cho nó:
                        bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                            number = count
                            backgroundColor = resources.getColor(R.color.g_blue)
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}