package com.example.aposs_admin.ui_controller.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.DialogChangePasswordBinding
import com.example.aposs_admin.ui_controller.account_manage_fragment.AccountManageViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChangePasswordDialog : DialogFragment() {

    private val viewModel: AccountManageViewModel by activityViewModels()
    private var binding: DialogChangePasswordBinding? = null
    private val args: ChangePasswordDialogArgs by navArgs()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val viewInflated: View = LayoutInflater.from(context)
                .inflate(R.layout.dialog_change_password, view as ViewGroup?, false)
            val password: TextInputEditText = viewInflated.findViewById(R.id.password)
            val rePassword: TextInputEditText = viewInflated.findViewById(R.id.rePassword)
            val btnConfirm: AppCompatButton = viewInflated.findViewById(R.id.confirm)
            val btnCancel: AppCompatButton = viewInflated.findViewById(R.id.cancel)
            btnConfirm.setOnClickListener {
                if (password.text.toString() == rePassword.text.toString()) {
                    viewModel.changePassword(args.account, password.text.toString()) {
                        Toast.makeText(this.requireContext(), "Thành công", Toast.LENGTH_SHORT)
                            .show()
                        getDialog()?.cancel()
                        findNavController().popBackStack()
                    }
                } else {
                    Toast.makeText(
                        this.requireContext(),
                        "Thông tin không chính xác",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            btnCancel.setOnClickListener {
                getDialog()?.cancel()
                findNavController().popBackStack()
            }
            builder.setView(viewInflated).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}