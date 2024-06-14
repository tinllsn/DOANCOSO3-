package com.example.admin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.admin.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

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

            viewProduct.setOnClickListener {
                val  intent = Intent(this@MainActivity,ViewProduct::class.java)
                startActivity(intent)
            }

            user.setOnClickListener {
                val intent = Intent(this@MainActivity, MainActivity2::class.java)
                startActivity(intent)
            }

            delete.setOnClickListener {
                val intent = Intent(this@MainActivity,deleteProduct::class.java)
                startActivity(intent)
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        mAuth = FirebaseAuth.getInstance()
        if (item.itemId == R.id.logout) {
            mAuth.signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return true
    }
}