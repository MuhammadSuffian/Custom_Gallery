package com.example.customgallery

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DetailedImage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detailed_image)
//        val Image=intent.getParcelableExtra<itemViewModel>("ItemsViewModel")
        if(Image!=null){
            val image_view=findViewById<ImageView>(R.id.image_slected)
            val text_name=findViewById<TextView>(R.id.image_name)
            image_view.setImageURI(Image.uri)
        }
    }
}