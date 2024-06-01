package com.example.doan.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doan.data.CartProduct
import com.example.doan.data.User
import com.example.doan.firebase.FirebaseCommon
import com.example.doan.util.Constants.USER_COLLECTION
import com.example.doan.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon,
    private var dbRef: DatabaseReference

) : ViewModel() {

    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    fun addUpdateProductInCart(cartProduct: CartProduct) {
        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }
//        realtime
        dbRef = FirebaseDatabase.getInstance().getReference("user").child(auth.uid!!).child("cart")

//         cần đổi addValueEventListener()
//        dbRef.addListenerForSingleValueEvent(object : ValueEventListener
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            //            goi khi thực hiện thành công
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                if (dataSnapshot.exists()) { //Add new product


                    for (product in dataSnapshot.children) {

                        val prod = product.getValue(CartProduct::class.java)
                        if (prod?.product?.id == cartProduct.product.id) {
                            val documentId = cartProduct.product.id

                            increaseQuantityReal(documentId, cartProduct)

                        } else {
                            addNewProductReal(cartProduct)


                        }

                    }

                } else {

                    addNewProductReal(cartProduct)


                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
                Log.wtf("FirebaseError", databaseError.message)
            }
        })

// ================================

//        Đoạn mã này truy vấn cơ sở dữ liệu Firestore để lấy tất cả các tài liệu trong bộ sưu tập "cart" của người dùng hiện tại.
        firestore.collection(USER_COLLECTION).document(auth.uid!!).collection("cart")
            .whereEqualTo("product.id", cartProduct.product.id).get()
            .addOnSuccessListener {
                it.documents.let {
//                  Nếu danh sách tài liệu trả về trống (không tìm thấy sản phẩm trong giỏ hàng),
                    //                  thì gọi hàm addNewProduct để thêm sản phẩm mới vào giỏ hàng.
                    if (it.isEmpty()) { //Add new product
//                        Log.wtf("detail","loi")
                        addNewProduct(cartProduct)
                    } else {
                        /*  Nếu có sản phẩm trong giỏ hàng, chúng ta lấy sản phẩm đầu tiên từ danh sách và kiểm tra xem sản phẩm trong giỏ hàng có giống với sản phẩm mới không.
                       Nếu giống nhau, chúng ta lấy ID của tài liệu chứa sản phẩm trong giỏ hàng và gọi increaseQuantity để tăng số lượng sản phẩm.
                        Nếu không giống nhau, chúng ta gọi addNewProduct để thêm sản phẩm mới vào giỏ hàng.  */

                        val product = it.first().toObject(CartProduct::class.java)
                        if (product == cartProduct) {
                            val documentId = it.first().id

                            increaseQuantity(documentId, cartProduct)
                        } else { //Add new product
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch { _addToCart.emit(Resource.Error(it.message.toString())) }
            }


    }

    private fun addNewProduct(cartProduct: CartProduct) {
        firebaseCommon.addProductToCart(cartProduct) { addedProduct, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(addedProduct!!))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    // realtime
    private fun addNewProductReal(cartProduct: CartProduct) {
        firebaseCommon.addProductToCartReal(cartProduct) { addedProduct, e ->
            viewModelScope.launch {

                if (e == null)
                    _addToCart.emit(Resource.Success(addedProduct!!))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    // ====
    private fun increaseQuantity(documentId: String, cartProduct: CartProduct) {
//        { _, e -> ... }: Đây là một hàm gọi lại (callback) được truyền vào phương thức increaseQuantity. Nó nhận hai tham số: dấu gạch dưới _
 //       (thường được sử dụng khi bạn không quan tâm đến giá trị) và e, đại diện cho một đối tượng Exception nếu có lỗi xảy ra trong quá trình tăng số lượng sản phẩm.
        firebaseCommon.increaseQuantity(documentId) { _, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(cartProduct))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    // realtime
    private fun increaseQuantityReal(documentId: String, cartProduct: CartProduct) {
        firebaseCommon.increaseQuantityReal(documentId) { _, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(cartProduct))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }
}
// ===









