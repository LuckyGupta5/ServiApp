package com.example.servivet.ui.main.adapter

import android.content.Context
import com.example.servivet.R
import com.example.servivet.data.model.calendar_models.CalendarDay
import com.example.servivet.databinding.MonthDaysLayoutBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class MonthAdapter(
    val requireContext: Context,
    val list: ArrayList<ListAdapterItem>,
    val calendarDays: ArrayList<String>
) :
    BaseAdapter<MonthDaysLayoutBinding, ListAdapterItem>(list) {

    override val layoutId: Int = R.layout.month_days_layout

    override fun bind(
        binding: MonthDaysLayoutBinding,
        item: ListAdapterItem?,
        position: Int
    ) {

        val date = calendarDays[position]
        binding.idDayText.text = date

        /* if(day.dayOfMonth.toString() =="0"){
             binding.idDayText.text = ""
         }else{
             binding.idDayText.text = day.dayOfMonth.toString()

         }*/




    }

    override fun getItemCount(): Int {
        return  30
    }
}