package com.example.servivet.ui.main.adapter.bank_module

import android.content.Context
import com.example.servivet.R
import com.example.servivet.data.model.bank_module.create_bank_account_list.response.UserBank
import com.example.servivet.databinding.CustomAddBankLayoutBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem
import com.google.gson.Gson

class AddedBankAccountAdapter(
    val list: ArrayList<UserBank>,
    val requireContext: Context,
    val onItemClick: (String, Int) -> Unit
) :
    BaseAdapter<CustomAddBankLayoutBinding, UserBank>(list) {
    override val layoutId: Int = R.layout.custom_add_bank_layout
    override fun bind(binding: CustomAddBankLayoutBinding, item: UserBank?, position: Int) {
        binding.apply {
            bankData = item

            idRemoveAccount.setOnClickListener {
                onItemClick(item?._id ?: "", 1)
                list.removeAt(position)
                notifyDataSetChanged()
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}