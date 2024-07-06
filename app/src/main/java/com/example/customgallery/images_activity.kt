package com.example.customgallery

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class images_activity : AppCompatActivity() {
    var dataobject = ArrayList<itemViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_images)

        val recyclerView = findViewById<RecyclerView>(R.id.recycleview)
        recyclerView.isNestedScrollingEnabled = false

        var op: Deferred<Unit>? = null
        if (!permission()) {
            askforpermission()
        } else {
            GlobalScope.launch(Dispatchers.Default) {

                op = async { dataobject = loadallPictures() }

                val wait = op?.await()
                if (wait != null) {
                    withContext(Dispatchers.Main) {
                        val recyclerAdapter = RecyclerItemViewModel(this@images_activity, dataobject)
                        recyclerView.layoutManager = GridLayoutManager(this@images_activity, 2)
                        recyclerView.adapter = recyclerAdapter
                        recyclerView.setItemViewCacheSize(50)

                        recyclerAdapter.onitemclick = { item ->
                            Log.d("Tag", "Clicked item: ${item.name}")
                            val intent = Intent(this@images_activity, DetailedImage::class.java)
                            intent.putExtra("ItemsViewModel", item)
                            startActivity(intent)
                        }

                        Log.d("Tag", "Handler Post and Data List Size is: " + dataobject.size)
                        Log.d("Tag", "Data Recieved and Data Size is: " + recyclerAdapter.arrContact.size)
                    }
                }
            }
        }
    }

    private fun askforpermission() {
        Log.d("Tag", "In AskPermission with android version: " + Build.VERSION.SDK_INT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 100)
        }
    }

    private fun loadallPictures(): ArrayList<itemViewModel> {
        Log.d("Tag", "In Load Pic")
        val templist = ArrayList<itemViewModel>()
        val url = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            }
            else -> {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        }
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
        )
        contentResolver.query(url, projection, null, null, null)?.use { cursor ->
            cursor?.let {
                while (cursor.moveToNext()) {
                    val picId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                    val picName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                    val picSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE))
                    val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, picId)
                    val model = itemViewModel(picId, picName, uri, picSize)
                    templist.add(model)
                }
            }
        }
        Log.d("Tag", "Load Pic End")
        return templist
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.d("Tag", "In On Request Permission")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadallPictures()
            }
        }
    }

    private fun permission(): Boolean {
        Log.d("Tag", "In Permission Function")
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                Log.d("Tag", "Android 13 and " + ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES))
                Log.d("Tag", "Permission: " + PackageManager.PERMISSION_GRANTED)
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            }
            else -> {
                false
            }
        }
    }
}
