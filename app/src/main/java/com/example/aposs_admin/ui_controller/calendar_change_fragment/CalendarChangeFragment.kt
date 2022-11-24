package com.example.aposs_admin.ui_controller.calendar_change_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.aposs_admin.R
import com.example.aposs_admin.adapter.CalendarAdapter
import com.example.aposs_admin.databinding.FragmentCalendarChangeBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CalendarChangeFragment : Fragment() {

    private val viewModel: CalendarChangeViewModel by viewModels()
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
        return binding?.root!!
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}