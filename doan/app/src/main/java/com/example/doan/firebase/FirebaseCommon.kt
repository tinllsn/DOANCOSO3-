package com.example.doan.firebase

import android.util.Log
import com.example.doan.data.CartProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,

) {

    private val cartCollection =
        firestore.collection("user").document(auth.uid!!).collection("cart")
//  thuộc tính cartCollection trỏ đến collection "cart"
    fun addProductToCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit) {
        cartCollection.document().set(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct, null)
            }.addOnFailureListener {
                onResult(null, it)
            }

    }
// realtime
    private val dbRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("user")
     private val cartCollectionReal = dbRef.child(auth.uid!!).child("cart")
    // Hàm để thêm sản phẩm vào giỏ hàng trong Realtime Database
    fun addProductToCartReal(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit) {
        // Tham chiếu đến nút "cart" trong Realtime Database của người dùng hiện tại
//         Dòng này cũng tạo ra một vị trí con mới với một khóa duy nhất, giống như dòng trước.
        //         Tuy nhiên, nó trả về một Reference tới vị trí mới đó thay vì truy cập vào khóa.
//        val cartCollectionPush =   cartCollectionReal.push()
        val cartCollectionPush = cartCollectionReal.child(cartProduct.product.id)

        cartCollectionPush.setValue(cartProduct)
            .addOnSuccessListener {
                onResult(cartProduct, null)
            }
            .addOnFailureListener {
                onResult(null, it)
            }
    }

//    realtime

    fun increaseQuantityReal(documentId: String, onResult: (String?, Exception?) -> Unit) {
        val productRef = cartCollectionReal.child(documentId)
        Log.wtf("firebasecommon", documentId )
        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val cartProduct = dataSnapshot.getValue(CartProduct::class.java)
                cartProduct?.let { product ->
                    val newQuantity = product.quantity + 1
                    productRef.child("quantity").setValue(newQuantity)
                        .addOnSuccessListener {
                            onResult(documentId, null)
                        }
                        .addOnFailureListener { exception ->
                            onResult(null, exception)
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onResult(null, Exception(databaseError.message))
            }
        })
    }


    fun decreaseQuantityReal(documentId: String, onResult: (String?, Exception?) -> Unit) {
        val productRef = cartCollectionReal.child(documentId)
        Log.wtf("firebasecommon", documentId )
        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val cartProduct = dataSnapshot.getValue(CartProduct::class.java)
                cartProduct?.let { product ->
                    val newQuantity = product.quantity - 1
                    productRef.child("quantity").setValue(newQuantity)
                        .addOnSuccessListener {
                            onResult(documentId, null)
                        }
                        .addOnFailureListener { exception ->
                            onResult(null, exception)
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onResult(null, Exception(databaseError.message))
            }
        })
    }


//    ====


    fun increaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        firestore.runTransaction { transition ->
            val documentRef = cartCollection.document(documentId)
            Log.wtf("chay_common",documentId)
            val document = transition.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transition.set(documentRef, newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    fun decreaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        firestore.runTransaction { transition ->
            val documentRef = cartCollection.document(documentId)
            val document = transition.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity - 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transition.set(documentRef, newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    enum class QuantityChanging {
        INCREASE,DECREASE
    }


}