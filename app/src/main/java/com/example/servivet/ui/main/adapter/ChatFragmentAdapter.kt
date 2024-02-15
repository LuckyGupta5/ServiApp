package com.example.servivet.ui.main.adapter

import android.content.Context
import androidx.core.view.isVisible
import com.example.servivet.R
import com.example.servivet.data.model.chat_models.request_list.response.Chatlist
import com.example.servivet.databinding.ChatRecyclerviewDesignBinding
import com.example.servivet.ui.base.BaseAdapter

class ChatFragmentAdapter(
    var context: Context,
    val chatList: ArrayList<Chatlist>,
    val check: Boolean,
    val onItemClick: (String, String) -> Unit
) : BaseAdapter<ChatRecyclerviewDesignBinding, Chatlist>(chatList) {


    override fun getItemCount(): Int {
        return chatList.size
    }

    override val layoutId: Int = R.layout.chat_recyclerview_design


    override fun bind(binding: ChatRecyclerviewDesignBinding, item: Chatlist?, position: Int) {
        binding.apply {
            idButtonContainer.isVisible = !check
            listData = item
            idAcceptBtn.setOnClickListener{onItemClick(context.getString(R.string.accept),chatList[position]._id)}
            idDeclineBtn.setOnClickListener{onItemClick(context.getString(R.string.decline),chatList[position]._id)}
        }
    }
}