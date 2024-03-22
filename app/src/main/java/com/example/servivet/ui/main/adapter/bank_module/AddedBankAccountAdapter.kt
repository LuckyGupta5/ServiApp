package com.example.servivet.ui.main.adapter.bank_module

import android.content.Context
import com.example.servivet.R
import com.example.servivet.data.model.bank_module.bank_list_response.response.Bank
import com.example.servivet.databinding.CustomAddBankLayoutBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class AddedBankAccountAdapter(
    val list: ArrayList<Bank>,
    val requireContext: Context,
    val onItemClick: (String, Int) -> Unit
) :
    BaseAdapter<CustomAddBankLayoutBinding, ListAdapterItem>(list) {
    override val layoutId: Int = R.layout.custom_add_bank_layout
    override fun bind(binding: CustomAddBankLayoutBinding, item: ListAdapterItem?, position: Int) {
    }

    override fun getItemCount(): Int {
        return list.size
    }
}