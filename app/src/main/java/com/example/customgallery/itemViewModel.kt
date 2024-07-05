package com.example.customgallery

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class itemViewModel(
    val id: Long,
    val name:String,
    val uri: Uri?,
    val picDate: Long,
    val picSize: Long)