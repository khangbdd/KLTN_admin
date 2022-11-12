package com.example.aposs_admin.ui_controller.dialog

import android.app.Activity
import android.app.AlertDialog
import com.example.aposs_admin.R

class LoadingDialog(myActivity: Activity) {
    private var activity: Activity = myActivity
    private lateinit var dialog: AlertDialog

    fun startLoading() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.dialog_loading, null))
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }

    fun dismissDialog() {
        dialog.dismiss()
    }
}