package com.example.customgallery

import android.Manifest
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Log.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    lateinit var btn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn = findViewById(R.id.button)
       // askwriteforpermission()
        askreadforpermission()
       // permission()
        btn.setOnClickListener {
            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show()
                val intent= Intent(this,images_activity::class.java)
                startActivity(intent)
          //  d("Tag", "Button Clicked")

        }
    }
//    fun perm(){
//        val intent = Intent(Intent.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
//        startActivityForResult(intent, REQUEST_STORAGE_ACCESS_CODE)
//    }
    private fun askreadforpermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 100)
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        }
    }
    fun askwriteforpermission() {
        Log.d("Tag", "In AskPermission with android version: " + Build.VERSION.SDK_INT)
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
             if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                 ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),100);
             }   //ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE), 100)
        }
         else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
           // ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE), 100)
        }
       Toast.makeText(this, "Wirte Permission: "+ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE),Toast.LENGTH_LONG).show()
       //Toast.makeText(this, ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE),Toast.LENGTH_LONG).show()
    }
//    private fun requestWriteMediaImagesPermission(imageUrl: String) {
//        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE), 100)
//    }
    private fun permission(): Boolean {
        Log.d("Tag", "In Permission Function")
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {

             //   Toast.makeText(this, "Android 13 and " + ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE),Toast.LENGTH_LONG).show()
              //  Toast.makeText(this, "Permission:  " +PackageManager.PERMISSION_GRANTED,Toast.LENGTH_LONG).show()
              //  Log.d("Tag", "Permission: " + PackageManager.PERMISSION_GRANTED)
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            }
            else -> {
                false
            }
        }
    }
}
