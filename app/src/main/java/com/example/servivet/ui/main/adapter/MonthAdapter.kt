package com.example.servivet.ui.main.adapter

import android.content.Context
import com.example.servivet.R
import com.example.servivet.databinding.MonthDaysLayoutBinding
import com.example.servivet.databinding.WeekDaysLayoutBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class MonthAdapter(val requireContext: Context, val list: ArrayList<ListAdapterItem>) :
    BaseAdapter<MonthDaysLayoutBinding, ListAdapterItem>(list) {

    override val layoutId: Int = R.layout.month_days_layout

    override fun bind(
        binding: MonthDaysLayoutBinding,
        item: ListAdapterItem?,
        position: Int
    ) {




    }

    override fun getItemCount(): Int {
        return  30
    }
}