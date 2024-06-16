package com.example.doan.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.doan.data.Category
import com.example.doan.util.Resource
import com.example.doan.viewmodel.CategoryViewModel
import com.example.doan.viewmodel.factory.BaseCategoryViewModelFactoryFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

// Được sử dụng để đánh dấu các thành phần Android mà bạn muốn Hilt inject dependencies vào
@AndroidEntryPoint
class AccessoryFragment: BaseCategoryFragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactoryFactory(firestore, Category.Accessory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
// lifecycleScope để chạy các coroutine
//        To do work concurrently in your app, you will be using Kotlin coroutines. Coroutines allow the execution of a block of code to be suspended and then resumed later,
//        so that other work can be done in the meantime
        lifecycleScope.launchWhenStarted {
//            viewModel.offerProducts. Được đặt trong một coroutine để đảm bảo rằng các thay đổi trạng thái
            //            dữ liệu được xử lý một cách bất đồng bộ và đúng đắn
//            Việc sử dụng collectLatest là bất đồng bộ vì nó chờ và phản ứng với các giá trị mới từ Flow.
            viewModel.offerProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showOfferLoading()
                    }
                    is Resource.Success -> {
//                        Khi viewModel.offerProducts trả về dữ liệu thành công (Resource.Success), it.data chứa danh sách dữ liệu mới nhất.
//offerAdapter.differ.submitList(it.data) được gọi để cập nhật danh sách sản phẩm trong offerAdapter.
//Sau khi cập nhật xong, hideOfferLoading() được gọi để ẩn đi tiến trình tải hoặc thông báo.
                        offerAdapter.differ.submitList(it.data)
                        hideOfferLoading()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG)
                            .show()
                        hideOfferLoading()
                    }
                    else -> Unit
                }
            }
        }


    }


}