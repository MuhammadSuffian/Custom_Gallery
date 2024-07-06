package com.example.customgallery

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class itemViewModel(
    val id: Long,
    val name:String,
    val uri: Uri?,
    val picSize: Long):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeParcelable(uri, flags)
        parcel.writeLong(picSize)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<itemViewModel> {
        override fun createFromParcel(parcel: Parcel): itemViewModel {
            return itemViewModel(parcel)
        }

        override fun newArray(size: Int): Array<itemViewModel?> {
            return arrayOfNulls(size)
        }
    }
}