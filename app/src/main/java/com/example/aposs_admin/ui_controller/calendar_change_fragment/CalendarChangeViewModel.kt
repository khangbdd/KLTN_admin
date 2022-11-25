package com.example.aposs_admin.ui_controller.calendar_change_fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.CalendarItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.Year
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class CalendarChangeViewModel @Inject constructor(): ViewModel() {

    val listCalenderItem: MutableLiveData<MutableList<CalendarItem>> = MutableLiveData(mutableListOf())

    val selectedMonth: MutableLiveData<String> = MutableLiveData()

    val selectedYear: MutableLiveData<String> = MutableLiveData()

    var availableMonth: ArrayList<String> = arrayListOf()

    var availableYear: ArrayList<String> = arrayListOf()

    init {
        val dateTime = LocalDateTime.now()
        selectedMonth.value = (dateTime.month.value + 1).toString()
        selectedYear.value = dateTime.year.toString()
    }

    fun loadAvailableMonth(minMonth: Int, year: Int, minYear: Int) {
        availableMonth = arrayListOf()
        val dateTime = LocalDateTime.now()
        if (selectedYear.value.toString() == dateTime.year.toString()) {
            var value = 1
            val currentMonth = dateTime.month.value + 1
            while (value <= currentMonth) {
                availableMonth.add(value.toString())
                value += 1
            }
        }
        if (selectedYear.value.toString() != year.toString() &&  selectedYear.value.toString() != minYear.toString()) {
            var value = minMonth
            while (value <= 12) {
                availableMonth.add(value.toString())
                value += 1
            }
        }
        if (selectedYear.value.toString() == minYear.toString()) {
            availableMonth.addAll(mutableListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"))
        }
    }

    private fun loadAvailableYear(year: Int, minYear: Int) {
        availableYear = arrayListOf()
        var value = minYear
        while (value <= year) {
            availableYear.add(value.toString())
            value += 1
        }
    }

    private fun loadDefaultCalendar() {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }
}