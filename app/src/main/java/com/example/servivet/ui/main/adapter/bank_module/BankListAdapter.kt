package com.example.servivet.ui.main.adapter.bank_module

import com.example.servivet.R
import com.example.servivet.databinding.CustomListLayoutBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class BankListAdapter(var list: ArrayList<ListAdapterItem>) :
    BaseAdapter<CustomListLayoutBinding, ListAdapterItem>(list) {
    override val layoutId: Int = R.layout.custom_list_layout
    override fun bind(binding: CustomListLayoutBinding, item: ListAdapterItem?, position: Int) {
    }

    override fun getItemCount(): Int {
        return return 6
    }
}