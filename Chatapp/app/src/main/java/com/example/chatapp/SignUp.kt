package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.chatapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private lateinit var binding: ActivitySignUpBinding
class SignUp : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        binding.btnSignup.setOnClickListener { 
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            val fname = binding.edtFirstname.text.toString()
            val lname = binding.edtLastname.text.toString()

            
            signup(fname,lname,email,password)
        }
    }

    private fun signup(fname:String,lname: String,email: String, password: String) {
       mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    addUserToDatabase(fname,lname,email,mAuth.currentUser?.uid!!)
                    val intent = Intent(this@SignUp,MainActivity::class.java)
                    startActivity(intent)
                    finish()



                } else {


                    Toast.makeText(this@SignUp,"Some error occured",Toast.LENGTH_SHORT).show()
                    
                }
            }
    }

    private fun addUserToDatabase(fname: String,lname:String, email: String, uid: String) {
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(uid).setValue(User(fname,lname,email,uid))
    }
}