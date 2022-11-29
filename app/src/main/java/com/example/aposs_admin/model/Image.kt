package com.example.aposs_admin.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class Image(var imgURL: String) : Parcelable {

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