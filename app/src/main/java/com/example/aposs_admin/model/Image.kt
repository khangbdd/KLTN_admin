package com.example.aposs_admin.model

import android.net.Uri
import java.io.Serializable

class Image(var imgURL: String) : Serializable {
    var imageUri: Uri? = null
        get() {
            field = Uri.parse(imgURL).buildUpon().scheme("https").build()
            return field
        }
    val uriRoot: Uri
        get() = Uri.parse(imgURL)

}