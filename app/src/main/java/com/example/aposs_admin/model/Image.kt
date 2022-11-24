package com.example.aposs_admin.model

import android.net.Uri
import java.io.Serializable

class Image(var imgURL: String) : Serializable {

    constructor(imageUri: Uri) : this("") {
        this.imageUri = imageUri
    }
    var imageUri: Uri? = null
        get() {
            return if(field == null){
                field = Uri.parse(imgURL).buildUpon().scheme("https").build()
                field
            }else{
                field
            }
        }
    val uriRoot: Uri
        get() = Uri.parse(imgURL)
}