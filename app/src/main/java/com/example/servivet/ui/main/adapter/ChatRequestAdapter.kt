package com.example.servivet.ui.main.adapter

import com.example.servivet.R
import com.example.servivet.databinding.ChatRequestDesignBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class ChatRequestAdapter(var list:ArrayList<ListAdapterItem>):BaseAdapter<ChatRequestDesignBinding,ListAdapterItem>(list) {
    override val layoutId: Int=R.layout.chat_request_design

    override fun bind(binding: ChatRequestDesignBinding, item: ListAdapterItem?, position: Int) {
        binding.apply {

        }
    }

    override fun getItemCount(): Int {
        return 11
    }
}