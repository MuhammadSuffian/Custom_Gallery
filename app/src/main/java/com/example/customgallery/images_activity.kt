package com.example.customgallery

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.io.File


class images_activity : AppCompatActivity() {
    var dataobject = ArrayList<itemViewModel>()
    private lateinit var deleteResultLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_images)

        var Del_item=false
        var uri:Uri
        var position=0
        var item1=intent.getParcelableExtra<itemViewModel>("ItemsViewModel")
        if(item1!=null){
          //  Toast.makeText(this,"File URI:${item1.uri}",Toast.LENGTH_SHORT).show()
          //  Toast.makeText(this,"File Path:${item1.uri?.path}",Toast.LENGTH_SHORT).show()
//            if(deleteFile(item1.name)){
//                Toast.makeText(this,"Deleted image",Toast.LENGTH_SHORT).show()
//            }
//            else{
//                Toast.makeText(this,"Fail in delete image:${item1.name}",Toast.LENGTH_SHORT).show()
//            }
           Del_item=true
            deleteResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.d("DeleteImage", "Image deleted successfully")
                } else {
                    Log.d("DeleteImage", "Image deletion failed")
                }
            }
            item1.uri?.let { deleteImageAPIabove30(this, it) }
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recycleview)
        recyclerView.isNestedScrollingEnabled = false

        var op: Deferred<Unit>? = null
        if (!permission()) {
            askreadforpermission()
        } else {
            GlobalScope.launch(Dispatchers.Default) {

                op = async { dataobject = loadallPictures() }

                val wait = op?.await()
                if (wait != null) {
                    withContext(Dispatchers.Main) {
                        if(Del_item){
                            dataobject.remove(item1)
                        }
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

    fun deleteImageAPIabove30(context: Context,imageUri: Uri) {
        if(Build.VERSION.SDK_INT>=30){
            val contentResolver = context.contentResolver
            var arrayList=ArrayList<Uri>()
            arrayList.add(imageUri)
            val intentSender = MediaStore.createDeleteRequest(contentResolver, arrayList).intentSender
            val senderRequest = IntentSenderRequest.Builder(intentSender)
                .setFillInIntent(null)
                .setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION, 0)
                .build()
            deleteResultLauncher.launch(senderRequest)
        }
    }

    @SuppressLint("Range")
//    fun deleteImage(context: Context, imageUri: Uri) {
////        if (imageUri != null) {
////          //  askwriteforpermission()
////            val count = contentResolver.delete(imageUri, null, null)
////            if (count > 0) {
////                Toast.makeText(context, "Image deleted.", Toast.LENGTH_SHORT).show()
////            } else {
////                Toast.makeText(context, "Failed to delete image.", Toast.LENGTH_SHORT).show()
////            }
////        } else {
////            Toast.makeText(context, "Invalid image URL.", Toast.LENGTH_SHORT).show()
////        }
//        val contentResolver: ContentResolver = context.contentResolver
//        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
//        val cursor = contentResolver.query(imageUri, projection, null, null, null)
//
//        cursor?.use {
//            if (it.moveToFirst()) {
//                val filePathColumnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
//                val filePath = it.getString(filePathColumnIndex)
//                val file = File(filePath)
//                if (file.exists()) {
//                    Toast.makeText(this,"Image found $filePath",Toast.LENGTH_SHORT).show()
//                    val deleted = file.delete()
//                    Toast.makeText(this,"Image Delete :$deleted",Toast.LENGTH_SHORT).show()
//                    if (deleted) {
//                        //val mediaStoreUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, it.getLong(it.getColumnIndex(MediaStore.Images.Media._ID)))
//                       // contentResolver.delete(mediaStoreUri, null, null)
//                        Toast.makeText(this,"image Delete Pass",Toast.LENGTH_SHORT).show()
//                    }
//                    Toast.makeText(this,"Image Delete Fail",Toast.LENGTH_SHORT).show()
//                }
//                else{
//                    Toast.makeText(this,"Image Delete Fail",Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//       // return false
////        val file = File(imageUri.path)
////        val found=file.exists()
////        if(found){
////            Toast.makeText(this,"Image found pass",Toast.LENGTH_SHORT).show()
////            val del= file.delete()
////        }
////        else{
////            Toast.makeText(this,"Image found fail",Toast.LENGTH_SHORT).show()
////        }
////       val del= file.delete()
////        if(del){
////            Toast.makeText(this,"Image Delete pass",Toast.LENGTH_SHORT).show()
////        }
////        else{
////            Toast.makeText(this,"Image Delete Fail",Toast.LENGTH_SHORT).show()
////        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE), 100)
        }
         else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.MANAGE_EXTERNAL_STORAGE), 100)
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
