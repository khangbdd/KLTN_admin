package com.example.aposs_admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aposs_admin.R
import com.example.aposs_admin.databinding.ItemCalendarBinding
import com.example.aposs_admin.model.CalendarItem

class CalendarAdapter(private val onClick: (calendarItem: CalendarItem) -> Unit): ListAdapter<CalendarItem, CalendarAdapter.CalendarViewHolder>(DiffCallBack.instance!!) {

    class DiffCallBack: DiffUtil.ItemCallback<CalendarItem>() {
        override fun areItemsTheSame(oldItem: CalendarItem, newItem: CalendarItem): Boolean {
            return oldItem.date == newItem.date && oldItem.month == newItem.month && oldItem.year == newItem.year && oldItem.isOff == newItem.isOff && oldItem.isNationalHoliday == newItem.isNationalHoliday && oldItem.isLocalHoliday == newItem.isLocalHoliday
        }

        override fun areContentsTheSame(oldItem: CalendarItem, newItem: CalendarItem): Boolean {
            return oldItem.date == newItem.date && oldItem.month == newItem.month && oldItem.year == newItem.year && oldItem.isOff == newItem.isOff && oldItem.isNationalHoliday == newItem.isNationalHoliday && oldItem.isLocalHoliday == newItem.isLocalHoliday
        }

        companion object {
            private var diffCallBack: DiffCallBack? = null
            val instance: DiffCallBack?
                get() {
                    if (diffCallBack == null) {
                        diffCallBack = DiffCallBack()
                    }
                    return diffCallBack
                }
        }
    }

    class CalendarViewHolder(val binding: ItemCalendarBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(calendarItem: CalendarItem) {
            binding.calendarItem = calendarItem
            if (calendarItem.isCurrentDay) {
                binding.root.setBackgroundColor(R.color.teal_200)
            }
            if (calendarItem.isLocalHoliday) {
                binding.isLocalHoliday.visibility = View.VISIBLE
            } else {
                binding.isLocalHoliday.visibility = View.GONE
            }
            if (calendarItem.isOff) {
                binding.isOff.visibility = View.VISIBLE
            } else {
                binding.isOff.visibility = View.GONE
            }
            if (calendarItem.isNationalHoliday) {
                binding.isNationalHoliday.visibility = View.VISIBLE
            } else {
                binding.isNationalHoliday.visibility = View.GONE
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemCalendarBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_calendar, parent,false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val calendarItem = getItem(position)
        holder.bind(calendarItem)
        holder.itemView.setOnClickListener{
            onClick(calendarItem)
        }
        with(holder) {
            if (calendarItem.isCurrentDay) {
                binding.root.setBackgroundColor(R.color.teal_200)
            }
            if (calendarItem.isLocalHoliday) {
                binding.isLocalHoliday.visibility = View.VISIBLE
            } else {
                binding.isLocalHoliday.visibility = View.GONE
            }
            if (calendarItem.isOff) {
                binding.isOff.visibility = View.VISIBLE
            } else {
                binding.isOff.visibility = View.GONE
            }
            if (calendarItem.isNationalHoliday) {
                binding.isNationalHoliday.visibility = View.VISIBLE
            } else {
                binding.isNationalHoliday.visibility = View.GONE
            }
        }
    }
}