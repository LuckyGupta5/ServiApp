package com.example.servivet.ui.main.adapter

import android.content.Context
import com.example.servivet.R
import com.example.servivet.databinding.CalenderConntainerLayoutBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class CalenderContainerAdapter(
    val requireContext: Context,
    val weekList: List<String>,
    val arrayList: ArrayList<ListAdapterItem>,
) : BaseAdapter<CalenderConntainerLayoutBinding, ListAdapterItem>(arrayList) {

    override val layoutId: Int = R.layout.calender_conntainer_layout

    override fun bind(
        binding: CalenderConntainerLayoutBinding,
        item: ListAdapterItem?,
        position: Int
    ) {
        binding.idWeekDaysRecycle.adapter = WeekAdapter(requireContext,ArrayList(),weekList)
        binding.idMonthDaysRecycle.adapter = MonthAdapter(requireContext,ArrayList())
    }

    override fun getItemCount(): Int {
        return  2
    }
}