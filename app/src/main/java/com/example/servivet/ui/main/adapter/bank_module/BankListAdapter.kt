package com.example.servivet.ui.main.adapter.bank_module

import android.content.Context
import com.example.servivet.R
import com.example.servivet.data.model.bank_module.bank_list_response.response.Bank
import com.example.servivet.databinding.CustomListLayoutBinding
import com.example.servivet.ui.base.BaseAdapter
import com.google.gson.Gson

class BankListAdapter(
    val bankList: ArrayList<Bank>,
    private val requireContext: Context,
    private val onItemClick: (String) -> Unit
) :
    BaseAdapter<CustomListLayoutBinding, Bank>(bankList) {

    override val layoutId: Int = R.layout.custom_list_layout
    override fun bind(binding: CustomListLayoutBinding, item: Bank?, position: Int) {

        binding.apply {
            bankData = item
            idContainer.setOnClickListener{onItemClick(Gson().toJson(item))}
        }
    }

    override fun getItemCount(): Int {
        return bankList.size
    }
}