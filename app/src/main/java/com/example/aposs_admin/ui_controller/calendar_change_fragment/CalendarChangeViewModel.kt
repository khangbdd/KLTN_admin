package com.example.aposs_admin.ui_controller.calendar_change_fragment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aposs_admin.model.CalendarItem
import com.example.aposs_admin.model.dto.DefaultCalendarDTO
import com.example.aposs_admin.network.AuthRepository
import com.example.aposs_admin.network.CalendarsRepository
import com.example.aposs_admin.util.LoadingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.time.LocalDateTime
import java.time.Year
import java.util.stream.Collectors
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class CalendarChangeViewModel @Inject constructor(
    private val calendarsRepository: CalendarsRepository,
    private val authRepository: AuthRepository
): ViewModel() {

    val listCalenderItem: MutableLiveData<MutableList<CalendarItem>> = MutableLiveData(mutableListOf())

    val selectedMonth: MutableLiveData<String> = MutableLiveData("")

    val selectedYear: MutableLiveData<String> = MutableLiveData("")

    var availableMonth: MutableLiveData<ArrayList<String>> = MutableLiveData(arrayListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"))

    var availableYear: MutableLiveData<ArrayList<String>> = MutableLiveData(arrayListOf())

    var currentDay: CalendarItem? = null

    val defaultCalendar: MutableLiveData<DefaultCalendarDTO> = MutableLiveData()


    init {
        loadDefaultCalendar()
    }

    fun loadAvailableMonth(minMonth: Int, minYear: Int, maxMonth: Int, maxYear: Int) {
        val availableMonthTempt = arrayListOf<String>()
        if (selectedYear.value.toString() == maxYear.toString()) {
            var value = 1
            while (value <= maxMonth) {
                availableMonthTempt.add(value.toString())
                value += 1
            }
        }
        if (selectedYear.value.toString() != maxYear.toString() &&  selectedYear.value.toString() != minYear.toString()) {
            availableMonthTempt.addAll(arrayListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"))
        }
        if (selectedYear.value.toString() == minYear.toString()) {
            var value = minMonth
            while (value <= 12) {
                availableMonthTempt.add(value.toString())
                value += 1
            }
        }
        availableMonth.postValue(availableMonthTempt)

    }

    private fun loadAvailableYear(minYear: Int, maxYear: Int) {
        val availableYearTempt = arrayListOf<String>()
        var value = minYear
        while (value <= maxYear) {
            availableYearTempt.add(value.toString())
            value += 1
        }
        availableYear.postValue(availableYearTempt)
    }

    private val loadDefaultStatus: MutableLiveData<LoadingStatus> = MutableLiveData()
    fun loadDefaultCalendar() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val accessToken = authRepository.getCurrentAccessTokenFromRoom()
                val response = calendarsRepository.getDefaultCalendars(accessToken.toString())
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        defaultCalendar.postValue(result)
                        listCalenderItem.postValue(result.getListCalendarItem())
                        selectedYear.postValue(result.currentYear.toString())
                        selectedMonth.postValue(result.currentMonth.toString())
                        withContext(Dispatchers.Main) {
                            loadAvailableYear(result.minYear, result.maxYear)
                            loadAvailableMonth(
                                result.minMonth,
                                result.minYear,
                                result.maxMonth,
                                result.maxYear
                            )
                        }
                        if (currentDay == null) {
                            currentDay = result.getListCalendarItem().stream()
                                .filter { it -> it.isCurrentDay }.findAny().get()
                        }
                    }
                    loadDefaultStatus.postValue(LoadingStatus.Success)
                } else {
                    if (response.code() == 401) {
                        if (authRepository.loadNewAccessTokenSuccess()) {
                            loadDefaultCalendar()
                        } else {
                            loadDefaultStatus.postValue(LoadingStatus.Fail)
                        }
                    } else {
                        loadDefaultStatus.postValue(LoadingStatus.Fail)
                    }
                }
            }catch (e: Exception){
                if (e is SocketTimeoutException) {
                    loadDefaultCalendar()
                } else {
                    loadDefaultStatus.postValue(LoadingStatus.Fail)
                    e.printStackTrace()
                    Log.e("Exception", e.toString())
                }
            }
        }
    }

    val updateStatus: MutableLiveData<LoadingStatus> = MutableLiveData()
    fun updateCalendar(calendarItem: CalendarItem, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val accessToken = authRepository.getCurrentAccessTokenFromRoom()
                val response = calendarsRepository.updateDateStatus(calendarItem.toCalendarItemDTO(), accessToken.toString())
                if (response.isSuccessful) {
                    updateStatus.postValue(LoadingStatus.Success)
                    loadCurrentDateWithSelectedMonthAndYear()
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                } else {
                    if (response.code() == 401) {
                        if (authRepository.loadNewAccessTokenSuccess()) {
                            updateCalendar(calendarItem, onSuccess)
                        } else {
                            updateStatus.postValue(LoadingStatus.Fail)
                        }
                    } else {
                        updateStatus.postValue(LoadingStatus.Fail)
                    }
                }
            }catch (e: Exception){
                if (e is SocketTimeoutException) {
                    updateCalendar(calendarItem, onSuccess)
                } else {
                    updateStatus.postValue(LoadingStatus.Fail)
                    e.printStackTrace()
                    Log.e("Exception", e.toString())
                }
            }
        }
    }

    val loadCurrentStatus: MutableLiveData<LoadingStatus> = MutableLiveData()
    fun loadCurrentDateWithSelectedMonthAndYear() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val accessToken = authRepository.getCurrentAccessTokenFromRoom()
                val response = calendarsRepository.getCalendarsList(selectedMonth.value.toString().toInt(), selectedYear.value.toString().toInt(), accessToken.toString())
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        listCalenderItem.postValue(result.stream().map{item -> item.toCalendarItem()}.collect(Collectors.toList()))
                    }
                    loadCurrentStatus.postValue(LoadingStatus.Success)
                } else {
                    if (response.code() == 401) {
                        if (authRepository.loadNewAccessTokenSuccess()) {
                            loadCurrentDateWithSelectedMonthAndYear()
                        } else {
                            loadCurrentStatus.postValue(LoadingStatus.Fail)
                        }
                    } else {
                        loadCurrentStatus.postValue(LoadingStatus.Fail)
                    }
                }
            }catch (e: Exception){
                if (e is SocketTimeoutException) {
                    loadCurrentDateWithSelectedMonthAndYear()
                } else {
                    loadCurrentStatus.postValue(LoadingStatus.Fail)
                    e.printStackTrace()
                    Log.e("Exception", e.toString())
                }
            }
        }
    }
}