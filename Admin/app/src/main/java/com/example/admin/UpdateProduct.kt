package com.example.admin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.admin.databinding.ActivityUpdateProductBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.UUID

private lateinit var binding: ActivityUpdateProductBinding

class UpdateProduct : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var key:String






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()


        val proID = intent.getStringExtra("id")
        firestore.collection("Products").whereEqualTo("id", proID)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.wtf("15/06/2024", "failed")
                    return@addSnapshotListener
                }

                for (doc in value!!) {
                    key = doc.id
                    val product = doc.toObject(Product::class.java)
                    binding.apply {
                        edName.setText(product.name)
                        edCategory.setText(product.category)
                        edPrice.setText(product.price.toString())
                        edOfferPercentage.setText(String.format("%.3f", product.offerPercentage ?: 0.0f))
                        edDescription.setText(product.description)

                    }
                }
                Log.d("15/06/2024", "Current cites in CA: ")
            }


        binding.btnSave.setOnClickListener {
            binding.apply {
                val name = edName.text.toString().trim()
                val category = edCategory.text.toString().trim()
                val price =  edPrice.text.toString().trim()
                val offer = edOfferPercentage.text.toString().trim()
                val des = edDescription.text.toString().trim()

                val product = Product(proID.toString(),name,category,price.toFloat(),offer.toFloat(),des,null,null,null)


                val updates = hashMapOf<String, Any>(
                    "name" to product.name,
                    "category" to product.category,
                    "price" to product.price,
                    "offerPercentage" to product.offerPercentage!!,
                    "description" to product.description!!
                )

                firestore.collection("Products").document(key).update(updates).addOnSuccessListener {
                    Log.d("Firestore", "DocumentSnapshot successfully updated!")
                }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error updating document", e)
                    }

            }


        }
    }
}