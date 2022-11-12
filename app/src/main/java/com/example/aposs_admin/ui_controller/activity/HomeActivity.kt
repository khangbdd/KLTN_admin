package com.example.aposs_admin.ui_controller.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.aposs_admin.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }
}