package com.example.aposs_admin.ui_controller.calendar_change_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aposs_admin.model.CalendarItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CalendarChangeViewModel @Inject constructor(): ViewModel() {

    val listCalenderItem: MutableLiveData<MutableList<CalendarItem>> = MutableLiveData(mutableListOf())
}