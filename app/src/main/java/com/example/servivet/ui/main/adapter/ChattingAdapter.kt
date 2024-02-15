package com.example.servivet.ui.main.adapter

import com.example.servivet.R
import com.example.servivet.databinding.ChatRequestDesignBinding
import com.example.servivet.databinding.CustomChattingViewBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class ChattingAdapter(var list: ArrayList<ListAdapterItem>) :
    BaseAdapter<CustomChattingViewBinding, ListAdapterItem>(list) {
    override val layoutId: Int = R.layout.custom_chatting_view

    override fun bind(binding: CustomChattingViewBinding, item: ListAdapterItem?, position: Int) {
        binding.apply {

        }
    }

    override fun getItemCount(): Int {
        return 6
    }
}