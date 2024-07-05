package com.example.customgallery

import android.content.Intent
import android.os.Bundle
import android.util.Log.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    lateinit var btn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn = findViewById(R.id.button)
        btn.setOnClickListener {
            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show()
                val intent= Intent(this,images_activity::class.java)
                startActivity(intent)
          //  d("Tag", "Button Clicked")

        }
    }
}
