package com.example.servivet.ui.main.adapter

import android.content.Context
import com.example.servivet.R
import com.example.servivet.databinding.WeekDaysLayoutBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class WeekAdapter(
    val requireContext: Context,
    val list: ArrayList<ListAdapterItem>,
    val weekList: List<String>
) :
    BaseAdapter<WeekDaysLayoutBinding, ListAdapterItem>(list) {

    override val layoutId: Int = R.layout.week_days_layout

    override fun bind(
        binding: WeekDaysLayoutBinding,
        item: ListAdapterItem?,
        position: Int
    ) {
        binding.weekName = weekList[position]


    }

    override fun getItemCount(): Int {
        return  weekList.size
    }
}