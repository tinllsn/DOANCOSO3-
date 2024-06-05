package com.example.doan.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doan.R
import com.example.doan.databinding.FragmentBaseCategoryBinding

import com.example.doan.adapter.*
import com.example.doan.util.*

open class BaseCategoryFragment: Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding
    protected val offerAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    protected val  bestProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }

//    Phương thức này được sử dụng để inflate layout của fragment. Nó trả về View của layout mà fragment sử dụng.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOfferRv()
        setupBestProductsRv()

        bestProductsAdapter.onClick = {
//             onClick cho cả hai adapter để điều hướng đến chi tiết sản phẩm khi nhấp vào một mục sản phẩm
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        offerAdapter.onClick = {

            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }
// dể thực hiện hành động phân trang khi người dùng cuộn xuống cuối danh sách.
        binding.rvOfferProducts.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1) && dx != 0){
                    onOfferPagingRequest()
                }
            }
        })
//    để xác định khi người dùng cuộn xuống cuối và kích hoạt phân trang
        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY){
               onBestProductsPagingRequest()
            }
        })
    }

    fun showOfferLoading(){
        binding.offerProductsProgressBar.visibility = View.VISIBLE
    }

    fun hideOfferLoading(){
        binding.offerProductsProgressBar.visibility = View.GONE
    }

    fun showBestProductsLoading(){
//        binding.bestProductsProgressBar.visibility = View.VISIBLE
    }

    fun hideBestProductsLoading(){
//        binding.bestProductsProgressBar.visibility = View.GONE
    }

    open fun onOfferPagingRequest(){

    }
//
    open fun onBestProductsPagingRequest(){

    }
//

    private fun setupBestProductsRv() {
//        binding.rvBestProducts.apply {
//            layoutManager =
//                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
//            adapter = bestProductsAdapter
//        }
    }

    private fun setupOfferRv() {
        binding.rvOfferProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = offerAdapter
        }
    }

    override fun onResume() {
        super.onResume()
//         để hiển thị thanh điều hướng khi fragment trở lại hoạt động.
        showBottomNavigationView()
    }

    }
