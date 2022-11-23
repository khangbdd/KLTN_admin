package com.example.aposs_admin.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
@Parcelize
class Image(var imgURL: String) : Parcelable {
    var imageUri: Uri? = null
        get() {
            field = Uri.parse(imgURL).buildUpon().scheme("https").build()
            return field
        }
    val uriRoot: Uri
        get() = Uri.parse(imgURL)

}