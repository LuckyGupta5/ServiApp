package com.example.servivet.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.example.servivet.R
import com.example.servivet.data.model.chat_models.request_list.response.Chatlist
import com.example.servivet.databinding.ChatRecyclerviewDesignBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.Session
import com.example.servivet.utils.dateDifferenceExample
import com.google.gson.Gson

class ChatFragmentAdapter(
    var context: Context,
    val chatList: ArrayList<Chatlist>,
    val check: Boolean,
    val onItemClick: (String, String) -> Unit
) : BaseAdapter<ChatRecyclerviewDesignBinding, Chatlist>(chatList) {
    private var filteredList: List<Chatlist> = chatList.toList()


    override fun getItemCount(): Int {
        return filteredList.size
    }

    override val layoutId: Int = R.layout.chat_recyclerview_design


    override fun bind(binding: ChatRecyclerviewDesignBinding, item: Chatlist?, position: Int) {

        val isDeletedByContainsId = item?.deletedBy?.any { id ->
            id == com.example.servivet.utils.Session.userDetails._id
        } ?: false

        val isSeenBy = item?.seenBy?.any { id ->
            id == com.example.servivet.utils.Session.userDetails._id
        } ?: false

        item?.createdAt = dateDifferenceExample(context, item?.createdAt) ?: ""

        binding.apply {
            item?.isSeenBy = isSeenBy
            idButtonContainer.isVisible = !check
            listData = filteredList[position]
            userDetails = com.example.servivet.utils.Session.userDetails
            idContainer.setOnClickListener {
                onItemClick(
                    context.getString(R.string.container), Gson().toJson(chatList[position])
                )
            }
            idAcceptBtn.setOnClickListener {
                onItemClick(
                    context.getString(R.string.accept), chatList[position]._id
                )
            }
            idDeclineBtn.setOnClickListener {
                onItemClick(
                    context.getString(R.string.decline), chatList[position]._id
                )
            }

            if (isDeletedByContainsId) {
                binding.idLastMsg.visibility = View.INVISIBLE
            } else {
                binding.idLastMsg.visibility = View.VISIBLE
            }
            Log.e("TAG", "checkSender: ${chatList[position].senderId.name}")
            Log.e("TAG", "checkReceiver: ${chatList[position].receiverId.name}")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(text: String) {
        filteredList = if (text.isEmpty()) {
            chatList
        } else {
            chatList.filter {
                it.senderId.name.contains(text, ignoreCase = true)||
                it.receiverId.name.contains(text, ignoreCase = true)
            }

        }
        notifyDataSetChanged()
    }
}