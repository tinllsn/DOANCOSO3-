package com.example.doan.fragments.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doan.adapter.BillingProductsAdapter
import com.example.doan.data.order.Order
import com.example.doan.data.order.OrderStatus
import com.example.doan.data.order.getOrderStatus
import com.example.doan.databinding.FragmentOrderDetailBinding
import com.example.doan.databinding.FragmentOrdersBinding
import com.example.doan.util.VerticalItemDecoration
import com.example.doan.util.hideBottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class OrderDetailFragment : Fragment(

) {
    private lateinit var binding: FragmentOrderDetailBinding
    private lateinit var mDbRef: FirebaseFirestore
    private  lateinit var auth: FirebaseAuth
//   khởi tạo một biến chỉ khi nó được sử dụng lần đầu tiên
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
// có thể truyền dữ liệu hoặc tham số bằng cách sử dụng "Safe Args", by navArgs<>() giúp đơn giản hóa việc truy cập đối số
    private val args by navArgs<OrderDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
//        Bundle là một cấu trúc dữ liệu dùng để lưu trữ và truyền dữ liệu giữa các Activity, Fragment
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = args.order
        hideBottomNavigationView()
        auth = FirebaseAuth.getInstance()
        setupOrderRv()

        binding.imageCloseOrder.setOnClickListener {
            findNavController().navigateUp()
        }

        // get order from ordets collection

        var originorderstatus: String? = null
        mDbRef = FirebaseFirestore.getInstance()

//        updateOrderStatus(order)

        mDbRef.collection("orders").addSnapshotListener { value, error ->
            if (error != null) {
                // Xử lý lỗi nếu có
                Log.e("TAG", "Listen failed.", error)
                return@addSnapshotListener
            }

            for (document in value!!) {
                val orderHandle = document.toObject(Order::class.java)
                if (orderHandle != null && orderHandle.orderId == order.orderId) {
//                    Log.wtf("07/6/2024", corderId)
                    originorderstatus = orderHandle.orderStatus
                    Log.wtf("762004",orderHandle.toString())
                    Log.wtf("762004", originorderstatus)
//                     update order in user collection

                    mDbRef.collection("user")
                        .document(auth.uid!!)
                        .collection("orders")
                        .whereEqualTo("orderId", order.orderId)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            for (document in querySnapshot.documents) {
                                // Cập nhật tài liệu
                                document.reference.update("orderStatus", originorderstatus)
                                    .addOnSuccessListener {
                                        Log.d("TAG", "DocumentSnapshot successfully updated!")
                                        binding.apply {

                                            tvOrderId.text = "Order #${order.orderId}"


                                            stepView.setSteps(
                                                mutableListOf(
                                                    OrderStatus.Ordered.status,
                                                    OrderStatus.Confirmed.status,
                                                    OrderStatus.Shipped.status,
                                                    OrderStatus.Delivered.status,
                                                )
                                            )

                                            val currentOrderState = when (originorderstatus?.let { getOrderStatus(it) }) {

                                                is OrderStatus.Ordered -> 0
                                                is OrderStatus.Confirmed -> 1
                                                is OrderStatus.Shipped -> 2
                                                is OrderStatus.Delivered -> 3

                                                else -> 0
                                            }

                                            Log.wtf("trc","trc")



                                            stepView.go(currentOrderState, false)


                                            if (currentOrderState == 3) {
                                                stepView.done(true)
                                            }

                                            tvFullName.text = order.address.fullName
                                            tvAddress.text = "${order.address.street} ${order.address.city}"
                                            tvPhoneNumber.text = order.address.phone

                                            tvTotalPrice.text = "$ ${order.totalPrice}"

                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("TAG", "Error updating document", e)
                                    }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("TAG", "Error getting documents: ", exception)
                        }
// ========================

                }
            }
        }





        billingProductsAdapter.differ.submitList(order.products)
    }

    private fun setupOrderRv() {
        binding.rvProducts.apply {
            adapter = billingProductsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }
}