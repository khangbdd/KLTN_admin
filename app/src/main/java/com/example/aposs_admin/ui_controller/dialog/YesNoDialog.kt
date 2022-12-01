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
import androidx.fragment.app.FragmentActivity
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
class YesNoDialog(
    private val content: String,
    private val email: String,
    private val owner: FragmentActivity
) : DialogFragment() {

    private val viewModel: AccountManageViewModel by activityViewModels()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(owner)
        // Get the layout inflater
        builder.setMessage(StringBuilder(content))
            .setPositiveButton(R.string.delete_confirm) { dialog, id ->
                viewModel.deleteAccount(email) {
                    Toast.makeText(this.requireContext(), "Thành công", Toast.LENGTH_SHORT)
                        .show()
                    getDialog()?.cancel()
                    findNavController().popBackStack()
                }
            }
            .setNegativeButton(R.string.delete_cancel) { dialog, id ->
                getDialog()?.cancel()
                findNavController().popBackStack()
            }
        return builder.create() ?: throw IllegalStateException("Activity cannot be null")
    }
}