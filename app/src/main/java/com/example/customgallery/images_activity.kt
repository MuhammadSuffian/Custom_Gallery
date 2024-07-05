package com.example.customgallery

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executors


class images_activity : AppCompatActivity() {
    var dataobject = ArrayList<itemViewModel>()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_images)


        val recyclerView = findViewById<RecyclerView>(R.id.recycleview)
        recyclerView.isNestedScrollingEnabled = false
//        recyclerAdapter = RecyclerItemViewModel(this, dataobject)


        val ExecutorService= Executors.newSingleThreadExecutor()
        if(!permission()){
            askforpermission()
        }
        ExecutorService.execute(){
            if(permission()){
                Log.d("Tag", "In Permission check")
                 dataobject=loadallPictures()
            }
            handler.post(Runnable {
                var recyclerAdapter: RecyclerItemViewModel
                recyclerAdapter = RecyclerItemViewModel(this, dataobject)
                recyclerView.layoutManager = GridLayoutManager(this,2)
                recyclerView.adapter = recyclerAdapter
                recyclerView.setItemViewCacheSize(50)
                recyclerAdapter.onitemclick={
                    val intent= Intent(this,DetailedImage::class.java)
                    intent.putExtra("itemViewModel",it)
                    startActivity(intent)
                }
                Log.d("Tag", "Handler Post and Data List Size is: "+dataobject.size)
                Log.d("Tag", "Data Recieved and Data Size is: "+recyclerAdapter.arrContact.size)
            })
        }
    }
    private fun askforpermission() {
        Log.d("Tag", "In AskPermission with adnroid verion: "+ Build.VERSION.SDK_INT)
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),100)
        else if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES ),100)
        }
    }

    private fun loadallPictures():ArrayList<itemViewModel>{
        Log.d("Tag", "In Load Pic")
        var templist= ArrayList<itemViewModel>()
        val url=when{
            Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q->{
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            }
            else->{
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
        }
        val projection=arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_TAKEN
        )
        contentResolver.query(url,projection,null,null,null)
            .use {
                    cursor->cursor?.let{
                while (cursor.moveToNext()){
                    val picId=cursor.getLong(cursor.getColumnIndexOrThrow( MediaStore.Images.Media._ID,))
                    val picName=cursor.getString(cursor.getColumnIndexOrThrow( MediaStore.Images.Media.DISPLAY_NAME,))
                    val picSize=cursor.getLong(cursor.getColumnIndexOrThrow( MediaStore.Images.Media.SIZE,))
                    val picDate=cursor.getLong(cursor.getColumnIndexOrThrow( MediaStore.Images.Media.DATE_TAKEN,))
                    val uri= ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,picId)
                    val model=itemViewModel(picId,picName,uri,picDate,picSize)
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
        if(requestCode==100){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                loadallPictures()
            }
            else{
                //if(ActivityCompat.shouldShowRequestPermissionRationale())
                //askforpermission()
            }
        }
    }
    private fun permission():Boolean {
        Log.d("Tag", "In Permission Function")
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
            Log.d("Tag", "Android 13 and "+ ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES))
            Log.d("Tag", "Permission: "+ PackageManager.PERMISSION_GRANTED)
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)== PackageManager.PERMISSION_GRANTED){
                Log.d("Tag", "Permission Granted")
                return true
            }
            else{
                return false
            }
        }
        else if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                return true
            }
            else{
                return false
            }
        }
        else{
            return false
        }
    }
}
