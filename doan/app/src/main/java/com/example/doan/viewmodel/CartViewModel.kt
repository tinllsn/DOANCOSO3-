package com.example.doan.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doan.data.CartProduct
import com.example.doan.data.Product
import com.example.doan.firebase.FirebaseCommon
import com.example.doan.helper.getProductPrice
//import com.example.doan.helper.getProductPrice
import com.example.doan.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _cartProducts =
        MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()


    val productsPrice = cartProducts.map {
        when (it) {
            is Resource.Success -> {
                calculatePrice(it.data!!)
            }

            else -> null
        }
    }
    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    private var cartProductDocuments = emptyList<DocumentSnapshot>()

// xóa cart khi sản phẩm về 0
    fun deleteCartProduct(cartProduct: CartProduct) {
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
            val documentId = cartProductDocuments[index].id


//            val db = FirebaseDatabase.getInstance().getReference("user").child(auth.uid!!).child("cart").child()

//            realtime

            val docRef = firestore.collection("user")
                .document(auth.uid!!)
                .collection("cart")
                .document(documentId)

            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Tài liệu tồn tại và có dữ liệu
//                                Khi bạn truy xuất một đối tượng lồng bên trong tài liệu Firestore, nó sẽ được trả về dưới dạng một Map<String, Any>.
                        Log.wtf("xoa 0",documentId)
                        val product = document.get("product") as Map<String, Any>
                        if (product != null) {
                            val productID = product["id"] as String?

                            FirebaseDatabase.getInstance().getReference("user").child(auth.uid!!).child("cart")
                                .child(productID.toString()).removeValue()
                                .addOnSuccessListener {
                                    // Xóa thành công

                                    Log.d("xoa 0", "xoa 0")
                                    firestore.collection("user").document(auth.uid!!).collection("cart")
                                        .document(documentId).delete()

                                }
                                .addOnFailureListener { exception ->
                                    // Xóa thất bại
                                    Log.e("xoa 0", "xoa 0", exception)
                                }

//                                    Log.d("new1", "Product Name: $productID")
                        }


                    } else {
                        // Không có tài liệu hoặc tài liệu không tồn tại
                        Log.d("TAG", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    // Xử lý khi có lỗi xảy ra
                    Log.d("TAG", "get failed with ", exception)
                }

//            =====



        }
    }

//     tính toán giá tiền
    private fun calculatePrice(data: List<CartProduct>): Float {
        return data.sumByDouble { cartProduct ->
            (cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price) * cartProduct.quantity).toDouble()
        }.toFloat()
    }


    init {
        getCartProducts()
    }

//    lấy các sản phẩm đã thêm vào giỏ hàng
    private fun getCartProducts() {
        viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch { _cartProducts.emit(Resource.Error(error?.message.toString())) }
                } else {
//                     id cart
                    cartProductDocuments = value.documents

                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch { _cartProducts.emit(Resource.Success(cartProducts)) }
                }
            }
    }


    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: FirebaseCommon.QuantityChanging
    ) {

        val index = cartProducts.value.data?.indexOf(cartProduct)

        /**
         * index could be equal to -1 if the function [getCartProducts] delays which will also delay the result we expect to be inside the [_cartProducts]
         * and to prevent the app from crashing we make a check
         */
        if (index != null && index != -1) {
            Log.wtf("index",index.toString())

            val documentId = cartProductDocuments[index].id
            Log.wtf("productid",documentId)

            when (quantityChanging) {
                FirebaseCommon.QuantityChanging.INCREASE -> {
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    increaseQuantity(documentId)


                    val docRef = firestore.collection("user")
                        .document(auth.uid!!)
                        .collection("cart")
                        .document(documentId)

                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                // Tài liệu tồn tại và có dữ liệu
//                                Khi bạn truy xuất một đối tượng lồng bên trong tài liệu Firestore, nó sẽ được trả về dưới dạng một Map<String, Any>.
                                val product = document.get("product") as Map<String, Any>
                                if (product != null) {
                                    val productID = product["id"] as String?
                                    increaseQuantityReal(productID.toString())
//                                    Log.d("new1", "Product Name: $productID")
                                }


                            } else {
                                // Không có tài liệu hoặc tài liệu không tồn tại
                                Log.d("TAG", "No such document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Xử lý khi có lỗi xảy ra
                            Log.d("TAG", "get failed with ", exception)
                        }



                }

                FirebaseCommon.QuantityChanging.DECREASE -> {
                    if (cartProduct.quantity == 1) {
                        viewModelScope.launch { _deleteDialog.emit(cartProduct) }
                        return
                    }
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    decreaseQuantity(documentId)

                    val docRef = firestore.collection("user")
                        .document(auth.uid!!)
                        .collection("cart")
                        .document(documentId)

                    docRef.get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                // Tài liệu tồn tại và có dữ liệu
//                                Khi bạn truy xuất một đối tượng lồng bên trong tài liệu Firestore, nó sẽ được trả về dưới dạng một Map<String, Any>.
                                val product = document.get("product") as Map<String, Any>
                                if (product != null) {
                                    val productID = product["id"] as String?
                                   decreaseQuantityReal(productID.toString())
//                                    Log.d("new1", "Product Name: $productID")
                                }



//                                val productName = document.getString("category") // Lấy thuộc tính "productName"
                                // Xử lý dữ liệu ở đây

                            } else {
                                // Không có tài liệu hoặc tài liệu không tồn tại
                                Log.d("TAG", "No such document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Xử lý khi có lỗi xảy ra
                            Log.d("TAG", "get failed with ", exception)
                        }

                }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId) { result, exception ->
            if (exception != null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId) { result, exception ->
            if (exception != null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
        }
    }
//  realtime
    private fun increaseQuantityReal(documentId: String) {
        firebaseCommon.increaseQuantityReal(documentId) { result, exception ->
            if (exception != null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
        }
    }

    private fun decreaseQuantityReal(documentId: String) {
        firebaseCommon.decreaseQuantityReal(documentId) { result, exception ->
            if (exception != null)
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
        }
    }
// ===================
}