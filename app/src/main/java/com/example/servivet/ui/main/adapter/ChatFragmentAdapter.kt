package com.example.servivet.ui.main.adapter

import android.content.Context
import android.se.omapi.Session
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.example.servivet.R
import com.example.servivet.data.model.chat_models.request_list.response.Chatlist
import com.example.servivet.data.model.connection.connection_list.responnse.UserDetail
import com.example.servivet.databinding.ChatRecyclerviewDesignBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.dateDifferenceExample
import com.google.gson.Gson

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

        val isDeletedByContainsId = item?.deletedBy?.any { id ->
            id == com.example.servivet.utils.Session.userDetails._id
        } ?: false

        val isSeenBy = item?.seenBy?.any { id ->
            id == com.example.servivet.utils.Session.userDetails._id
        } ?: false

        item?.createdAt = dateDifferenceExample(context,item?.createdAt)?:""

        binding.apply {
            item?.isSeenBy = isSeenBy
            idButtonContainer.isVisible = !check
            listData = item
            userDetails = com.example.servivet.utils.Session.userDetails
            idContainer.setOnClickListener { onItemClick(context.getString(R.string.container), Gson().toJson(chatList[position])) }
            idAcceptBtn.setOnClickListener { onItemClick(context.getString(R.string.accept), chatList[position]._id) }
            idDeclineBtn.setOnClickListener {
                onItemClick(
                    context.getString(R.string.decline),
                    chatList[position]._id
                )
            }

            if (isDeletedByContainsId) {
                binding.idLastMsg.visibility = View.INVISIBLE
            } else {
                binding.idLastMsg.visibility = View.VISIBLE
            }


        }
    }
}