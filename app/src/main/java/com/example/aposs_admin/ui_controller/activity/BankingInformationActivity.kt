package com.example.aposs_admin.ui_controller.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.ActivityBankingInformationBinding
import com.example.aposs_admin.ui_controller.dialog.LoadingDialog
import com.example.aposs_admin.util.LoadingStatus
import com.example.aposs_admin.viewmodels.OnlineCheckOutInformationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BankingInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBankingInformationBinding
    private val viewModel: OnlineCheckOutInformationViewModel by viewModels()

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBankingInformationBinding.inflate(layoutInflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setContentView()
        observerErrorMessage()
        setUpLoadingDialog()

        setContentView(binding.root)
    }

    private fun setUpLoadingDialog() {
        // create loading dialog
        loadingDialog = LoadingDialog(this)
        // tracking loading status
        viewModel.updateStatus.observe(this) {
            if (it == LoadingStatus.Loading) {
                loadingDialog.startLoading()
            } else {
                loadingDialog.dismissDialog()
                if (it == LoadingStatus.Success) {
                    Toast.makeText(this, "Thay đổi thông tin thanh toán thành công!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, "Thay đổi thông tin thanh toán thất bại!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


    private fun setContentView(){
        binding.back.setOnClickListener {
            this.finish()
        }
        binding.saveNewBankingInformation.setOnClickListener {
            viewModel.changeBankingInformation()
        }

        binding.bankName.doAfterTextChanged {
            viewModel.checkChangeable()
        }
        binding.receiver.doAfterTextChanged {
            viewModel.checkChangeable()
        }
        binding.accountNumber.doAfterTextChanged {
            viewModel.checkChangeable()
        }
        binding.branchName.doAfterTextChanged {
            viewModel.checkChangeable()
        }
    }

    private fun observerErrorMessage(){
        viewModel.errorMessage.observe(this) {
            if (it != null){
                Toast.makeText(this, it , Toast.LENGTH_SHORT).show()
            }
        }
    }
}