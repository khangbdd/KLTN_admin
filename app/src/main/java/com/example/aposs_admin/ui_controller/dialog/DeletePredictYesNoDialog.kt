package com.example.aposs_admin.ui_controller.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultOwner
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.aposs_admin.R
import com.example.aposs_admin.model.dto.PredictionDetailDTO
import com.example.aposs_admin.model.dto.PredictionRecordDTO
import com.example.aposs_admin.ui_controller.account_manage_fragment.AccountManageViewModel
import com.example.aposs_admin.ui_controller.list_predict_fragment.ListPredictViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeletePredictYesNoDialog (
    private val owner: Context,
    private val content: String,
    private val predictionRecordDTO: PredictionDetailDTO,
    private val navController: NavController
    ): DialogFragment() {
    private val viewModel: ListPredictViewModel by activityViewModels()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(owner)
        // Get the layout inflater
        builder.setMessage(StringBuilder(content))
            .setPositiveButton(R.string.delete_confirm) { dialog, id ->
                viewModel.deletePredictRecord(predictionRecordDTO) {
                    Toast.makeText(this.requireContext(), "Xóa thành công", Toast.LENGTH_SHORT)
                        .show()
                    getDialog()?.cancel()
                    navController.popBackStack()
                }
            }
            .setNegativeButton(R.string.delete_cancel) { dialog, id ->
                getDialog()?.cancel()
            }
        return builder.create() ?: throw IllegalStateException("Activity cannot be null")
    }
}