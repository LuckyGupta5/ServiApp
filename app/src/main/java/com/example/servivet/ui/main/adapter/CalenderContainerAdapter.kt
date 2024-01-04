package com.example.servivet.ui.main.adapter

import android.content.Context
import android.util.Log
import com.example.servivet.R
import com.example.servivet.databinding.CalenderConntainerLayoutBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem
import com.google.gson.Gson

class CalenderContainerAdapter(
    val requireContext: Context,
    val weekList: List<String>,
    val calendarDays: ArrayList<String>,
    val arrayList: ArrayList<ListAdapterItem>,
) : BaseAdapter<CalenderConntainerLayoutBinding, ListAdapterItem>(arrayList) {

    override val layoutId: Int = R.layout.calender_conntainer_layout

    override fun bind(
        binding: CalenderConntainerLayoutBinding,
        item: ListAdapterItem?,
        position: Int
    ) {
        Log.e("TAG", "checkDays: ${Gson().toJson(calendarDays)}", )
        binding.idWeekDaysRecycle.adapter = WeekAdapter(requireContext,ArrayList(),weekList)
        binding.idMonthDaysRecycle.adapter = MonthAdapter(requireContext,ArrayList(),calendarDays)
    }

    override fun getItemCount(): Int {
        return  2
    }
}