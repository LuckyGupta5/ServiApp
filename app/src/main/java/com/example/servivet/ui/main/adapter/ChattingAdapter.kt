package com.example.servivet.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.data.model.chat_models.chat_list.ChatMessage
import com.example.servivet.databinding.CustomChattingViewBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.Session
import com.example.servivet.utils.convertTimeStampToTime
import com.example.servivet.utils.getCurrentTimeInFormat
import com.example.servivet.utils.interfaces.ListAdapterItem
import com.google.gson.Gson

class ChattingAdapter(val requireContext: Context, var chattingList: ArrayList<ChatMessage>) :
    BaseAdapter<CustomChattingViewBinding, ChatMessage>(chattingList) {
    override val layoutId: Int = R.layout.custom_chatting_view

    @SuppressLint("SuspiciousIndentation")
    override fun bind(binding: CustomChattingViewBinding, item: ChatMessage?, position: Int) {
        binding.apply {

        }
        if (chattingList[position].senderId._id != Session.userDetails._id) {
            //  Log.d("TAG", "onBindViewHolder451454: ${Gson().toJson(chattingList[position].receiverId)} /n $_id")
            binding.idReceiverLayout.isVisible = true
            binding.idSenderLayout.isVisible = false
            binding.idReceiverMessage.text = chattingList[position].message
            binding.idReceiverTime.text = convertTimeStampToTime(chattingList[position].createdAt, 10, 0)
            if(chattingList[position].file !=null && chattingList[position].file.isNotEmpty())
             Glide.with(requireContext).load(chattingList[position].file[0]).placeholder(R.drawable.userprofile).into(binding.idReceiverImage)
        } else {

            binding.idReceiverLayout.isVisible = false
            binding.idSenderLayout.isVisible = true
            binding.idSenderMessage.text = chattingList[position].message
            binding.idSenderTime.text = convertTimeStampToTime(chattingList[position].createdAt, 10, 0)
            if(chattingList[position].file !=null && chattingList[position].file.isNotEmpty())
            Glide.with(requireContext).load(chattingList[position].file[0]).placeholder(R.drawable.userprofile).into(binding.idSenderImage)


        }
    }


    fun updateList(chatList: java.util.ArrayList<ChatMessage>) {
        chattingList = chatList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return chattingList.size
    }
}