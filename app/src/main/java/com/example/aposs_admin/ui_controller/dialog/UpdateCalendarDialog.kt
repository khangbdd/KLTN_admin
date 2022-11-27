package com.example.aposs_admin.ui_controller.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.DialogAddAccountBinding
import com.example.aposs_admin.model.CalendarItem
import com.example.aposs_admin.ui_controller.account_manage_fragment.AccountManageViewModel
import com.example.aposs_admin.ui_controller.calendar_change_fragment.CalendarChangeViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class UpdateCalendarDialog(): DialogFragment() {

    private val viewModel: CalendarChangeViewModel by activityViewModels()
    private val args: UpdateCalendarDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater;
            val viewInflated: View = LayoutInflater.from(context)
                .inflate(R.layout.dialog_update_calendar, view as ViewGroup?, false)
            val isOff: CheckBox = viewInflated.findViewById(R.id.cb_isOff)
            val isLocalHoliday: CheckBox = viewInflated.findViewById(R.id.cb_isLocalHoliday)
            val description: TextInputEditText = viewInflated.findViewById(R.id.description)
            val btnConfirm: AppCompatButton = viewInflated.findViewById(R.id.confirm)
            val btnCancel: AppCompatButton = viewInflated.findViewById(R.id.cancel)
            isOff.isChecked = args.calendarItem.isOff
            isLocalHoliday.isChecked = args.calendarItem.isLocalHoliday
            description.setText(args.calendarItem.description)
            btnConfirm.setOnClickListener {
                val newCalendarItem = args.calendarItem
                newCalendarItem.isOff = isOff.isChecked
                newCalendarItem.isLocalHoliday = isLocalHoliday.isChecked
                newCalendarItem.description = description.text.toString()
                viewModel.updateCalendar(newCalendarItem) {
                    Toast.makeText(requireContext(), "Thành công", Toast.LENGTH_SHORT).show()
                    dialog?.cancel()
                    findNavController().popBackStack()
                }
            }
            btnCancel.setOnClickListener {
                dialog?.cancel()
                findNavController().popBackStack()
            }
            builder.setTitle("Update for ${args.calendarItem.date}-${args.calendarItem.month}-${args.calendarItem.year}").setView(viewInflated).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}