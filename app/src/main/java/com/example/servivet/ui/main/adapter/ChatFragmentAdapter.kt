package com.example.servivet.ui.main.adapter

import android.content.Context
import com.example.servivet.R
import com.example.servivet.databinding.ChatRecyclerviewDesignBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.interfaces.ListAdapterItem

class ChatFragmentAdapter(var context:Context,var list:ArrayList<ListAdapterItem>):BaseAdapter<ChatRecyclerviewDesignBinding,ListAdapterItem>(list) {


    inner class ClickAction(var position: Int){

    }

    override fun getItemCount(): Int {
        return 7
    }
    override val layoutId: Int= R.layout.chat_recyclerview_design


    override fun bind(binding: ChatRecyclerviewDesignBinding, item: ListAdapterItem?, position: Int, ) {
        binding.apply {
            binding.click=ClickAction(position)

        }
    }
}