package com.example.customgallery

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DetailedImage : AppCompatActivity() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detailed_image)
        val item=intent.getParcelableExtra<itemViewModel>("ItemsViewModel")
        if(item!=null){
            val image_view=findViewById<ImageView>(R.id.image_slected)
            val text_view=findViewById<TextView>(R.id.image_name)
            val text_viewv1=findViewById<TextView>(R.id.image_size)
            val text_viewv3=findViewById<TextView>(R.id.Image_ID)
            val Del_vtn=findViewById<Button>(R.id.Delete_Btn)
            image_view.setImageURI(item.uri)
            text_view.text="Name: " + item.name
            text_viewv3.text="ID: "+item.id.toString()
          var  picsize:Double = ((item.picSize/1024)).toDouble()
            if(picsize<1024.0){
                text_viewv1.text= "Size: "+picsize.toString()+" KB"
            }
            else{
                picsize=picsize/1024
                text_viewv1.text= "Size:"+picsize.toString()+" MB"
            }
           Del_vtn.setOnClickListener(){
               Toast.makeText(this,"Del Btn Clicked",Toast.LENGTH_SHORT).show()
               try{
                  // item.uri?.let { contentResolver.delete(it,null,null) }

               }
               catch(e: Exception){
                   Toast.makeText(this,"Deleletion FAIL WITH EXCEPTION:$e ",Toast.LENGTH_LONG).show()
               }

           }

        }
    }
}