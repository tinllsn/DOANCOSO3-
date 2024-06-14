package com.example.admin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.admin.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityLoginBinding

class Login : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)


        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            if (email.isEmpty() ) {
                binding.edtEmail.error = "Email is required."
                binding.edtEmail.requestFocus()
            Toast.makeText(this@Login,"Empty",Toast.LENGTH_SHORT).show()
            }

            else if (password.isEmpty()) {
                binding.edtPassword.error = "Password is required."
                binding.edtPassword.requestFocus()
                Toast.makeText(this@Login,"Empty",Toast.LENGTH_SHORT).show()
            } else {
                login(email,password)
            }


        }
        binding.btnSignup.setOnClickListener {
            val intent = Intent(this@Login,SignUp::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val userId = mAuth.currentUser?.uid ?: return@addOnCompleteListener
                    val userDoc = db.collection("user").document(userId)
//====

                    userDoc.get().addOnSuccessListener { document ->
                        if (document != null) {
                            val role = document.getLong("role")?.toInt()
                            if (role != null && role == 1) {
                                // User is admin, navigate to MainActivity
                                val intent = Intent(this@Login, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                // User is not admin, show error message
                                Toast.makeText(this@Login, "You do not have admin privileges.", Toast.LENGTH_SHORT).show()
                                mAuth.signOut() // Optional: Sign out the non-admin user
                            }
                        }
                    }

//                    ====
//                    val intent = Intent(this@Login,MainActivity::class.java)
//                    startActivity(intent)
//                    finish()

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@Login, "User does not exist",Toast.LENGTH_SHORT).show()
                }
            }
    }
}