package com.example.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.admin.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private lateinit var binding: ActivitySignUpBinding

class SignUp : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseFirestore.getInstance() // Khởi tạo mDbRef đúng cách

        binding.btnSignup.setOnClickListener {
            val fname = binding.edtFirstname.text.toString().trim()
            val lname = binding.edtLastname.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()
            val pass = binding.edtPassword.text.toString().trim()

            if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this@SignUp, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                val user = User(fname, lname, email)
                signup(user, pass)
            }
        }
    }

    private fun signup(user: User, pass: String) {
        mAuth.createUserWithEmailAndPassword(user.email, pass)
            .addOnSuccessListener {
                it.user?.let {
                    addUserToDatabase(user, it.uid)
                    val intent = Intent(this@SignUp, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener {
                Toast.makeText(this@SignUp, "Signup Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addUserToDatabase(user: User, uid: String) {
        mDbRef.collection("user")
            .document(uid)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this@SignUp, "User added successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this@SignUp, "Failed to add user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
