package com.example.admin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.admin.databinding.ActivityLoginBinding
import com.example.admin.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityMainBinding
private lateinit var mAuth: FirebaseAuth
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            addProduct.setOnClickListener {
                val intent1 = Intent(this@MainActivity,AddProduct::class.java)
                startActivity(intent1)

            }

            order.setOnClickListener {
                val intent = Intent(this@MainActivity,HandleOrder::class.java)
                startActivity(intent)
            }

            deleteProduct.setOnClickListener {
                val  intent = Intent(this@MainActivity,DeleteProduct::class.java)
                startActivity(intent)
            }

            user.setOnClickListener {
                val intent = Intent(this@MainActivity, MainActivity2::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            mAuth.signOut()
            var intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return true
    }
}