package com.example.aposs_admin.ui_controller.calendar_change_fragment

import android.os.Bundle
import android.util.Log
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
import com.example.aposs_admin.util.LoadingStatus
import dagger.hilt.android.AndroidEntryPoint
import java.util.stream.Collectors


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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_calendar_change, container, false)
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
        setUpListCalendar()
        setUpMonthYearSelection()
        binding?.back?.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding?.root!!
    }
    private val adapter = CalendarAdapter { calendarItem ->
        findNavController().navigate(
            CalendarChangeFragmentDirections.actionCalendarChangeFragmentToUpdateCalendarDialog(
                calendarItem
            )
        )
    }
    fun setUpListCalendar() {
        binding?.rcCalendars?.adapter = adapter
    }


    fun setUpMonthYearSelection() {
        val adapterMonth: ArrayAdapter<String> = ArrayAdapter(
            requireContext(), R.layout.item_auto_complete, ArrayList(
                viewModel.availableMonth.value!!
            )
        )
        val adapterYear: ArrayAdapter<String> = ArrayAdapter(
            requireContext(), R.layout.item_auto_complete, ArrayList(
                viewModel.availableYear.value!!
            )
        )
        binding?.month?.setAdapter(adapterMonth)
        binding?.year?.setAdapter(adapterYear)
        binding?.year?.setOnItemClickListener { parent, view, position, id ->
            viewModel.selectedYear.value = viewModel.availableYear.value!![position]
            viewModel.defaultCalendar.value?.let {
                viewModel.loadAvailableMonth(it.minMonth, it.minYear, it.maxMonth, it.maxYear)
                adapterMonth.clear()
                adapterMonth.addAll(viewModel.availableMonth.value!!)
                adapterMonth.notifyDataSetChanged()
            }
            viewModel.loadCurrentDateWithSelectedMonthAndYear()
        }
        binding?.month?.setOnItemClickListener { parent, view, position, id ->
            viewModel.selectedMonth.value = viewModel.availableMonth.value!![position]
            viewModel.defaultCalendar.value?.let {
               viewModel.loadCurrentDateWithSelectedMonthAndYear()
            }
        }
        viewModel.availableMonth.observe(viewLifecycleOwner) {
            binding?.month?.setText(viewModel.selectedMonth.value, false)
            adapterMonth.clear()
            adapterMonth.addAll(it)
            adapterMonth.notifyDataSetChanged()
        }
        viewModel.availableYear.observe(viewLifecycleOwner) {
            binding?.year?.setText(viewModel.selectedYear.value, false)
            binding?.month?.setText(viewModel.selectedMonth.value, false)
            adapterYear.clear()
            adapterYear.addAll(it)
            adapterYear.notifyDataSetChanged()
        }
        viewModel.loadCurrentStatus.observe(viewLifecycleOwner) {
            if (it == LoadingStatus.Success) {
                adapter.notifyDataSetChanged()
            }
        }
    }

//    fun resetAdapter(adapter: ArrayAdapter<String>, data: ArrayList<String>) {
//        adapter.clear()
//        adapter.addAll(data)
//        adapter.notifyDataSetChanged()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.loadDefaultCalendar()
    }
}