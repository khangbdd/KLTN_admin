package com.example.aposs_admin.ui_controller.dialog

import android.app.AlertDialog
import android.app.Dialog
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
import com.example.aposs_admin.databinding.DialogAddAccountBinding
import com.example.aposs_admin.ui_controller.account_manage_fragment.AccountManageViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class NewAccountDialog: DialogFragment() {

    private val viewModel: AccountManageViewModel by activityViewModels()
    private var binding: DialogAddAccountBinding? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val viewInflated: View = LayoutInflater.from(context)
                .inflate(R.layout.dialog_add_account, view as ViewGroup?, false)
            val email: TextInputEditText = viewInflated.findViewById(R.id.email)
            val password: TextInputEditText = viewInflated.findViewById(R.id.password)
            val btnConfirm: AppCompatButton = viewInflated.findViewById(R.id.confirm)
            val btnCancel: AppCompatButton = viewInflated.findViewById(R.id.cancel)
            btnConfirm.setOnClickListener {
                if (isEmailRightFormat(email.text.toString())) {
                    viewModel.addNewAccount(email.text.toString(), password.text.toString()) {
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

    private fun isEmailRightFormat(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }
}