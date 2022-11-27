package com.example.aposs_admin.ui_controller.calendar_change_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.CalendarAdapter
import com.example.aposs_admin.databinding.FragmentCalendarChangeBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CalendarChangeFragment : Fragment() {

    private val viewModel: CalendarChangeViewModel by activityViewModels()
    private var binding: FragmentCalendarChangeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar_change, container, false)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        setUpListCalendar()
        setUpMonthYearSelection()
        return binding?.root!!
    }

    fun setUpListCalendar() {
        binding?.rcCalendars?.adapter = CalendarAdapter { calendarItem ->
            findNavController().navigate(CalendarChangeFragmentDirections.actionCalendarChangeFragmentToUpdateCalendarDialog(calendarItem))
        }
    }

    fun setUpMonthYearSelection() {
        val adapterMonth = ArrayAdapter(
            requireContext(), R.layout.item_auto_complete, ArrayList(
                viewModel.availableMonth
            )
        )

        val adapterYear = ArrayAdapter(
            requireContext(), R.layout.item_auto_complete, ArrayList(
                viewModel.availableYear
            )
        )
        binding?.month?.setAdapter(adapterMonth)
        binding?.year?.setAdapter(adapterYear)
        binding?.year?.setOnItemClickListener { parent, view, position, id ->
            viewModel.defaultCalendar.value?.let {
                viewModel.loadAvailableMonth(it.minMonth, it.minYear, it.maxMonth, it.maxYear)
                adapterMonth.clear()
                adapterMonth.addAll(viewModel.availableMonth)
                adapterMonth.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.loadDefaultCalendar()
    }
}