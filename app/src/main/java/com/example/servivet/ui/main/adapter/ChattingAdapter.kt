package com.example.servivet.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.servivet.R
import com.example.servivet.data.model.chat_models.chat_list.ChatMessage
import com.example.servivet.data.model.chat_models.manual_chating_objest.ManualUserDataClass
import com.example.servivet.databinding.CustomChattingViewBinding
import com.example.servivet.ui.base.BaseAdapter
import com.example.servivet.utils.Session
import com.example.servivet.utils.convertTimeStampToTime
import com.google.gson.Gson

class ChattingAdapter(
    val requireContext: Context,
    var chattingList: ArrayList<ChatMessage>,
    var onItemClick: (Int, String) -> Unit,
    val manualUserDataClass: ManualUserDataClass
) :
    BaseAdapter<CustomChattingViewBinding, ChatMessage>(chattingList) {
    override val layoutId: Int = R.layout.custom_chatting_view

    @SuppressLint("SuspiciousIndentation")
    override fun bind(binding: CustomChattingViewBinding, item: ChatMessage?, position: Int) {
        binding.apply {
            //  item?.createdAt = convertTimeStampToTime(chattingList[position].createdAt, 10, 0) ?: ""
            if (item?.file?.isNotEmpty() == true)
                item.containMP4 = item.file[0].endsWith(".mp4")

            binding.messageData = chattingList[position]
            binding.userDetails = Session.userDetails
            binding.idSenderMedia.setOnClickListener {

                onItemClick(0, Gson().toJson(item?.file))
            }
            binding.idMedia.setOnClickListener { onItemClick(0, Gson().toJson(item?.file)) }


        }
        if (chattingList[position].senderId._id != Session.userDetails._id) {
            binding.idReceiverLayout.isVisible = true
            binding.idSenderLayout.isVisible = false
            binding.idReceiverMessage.text = chattingList[position].message
            binding.idReceiverTime.text =
                convertTimeStampToTime(chattingList[position].createdAt, 10, 0)
            Glide.with(requireContext).load(manualUserDataClass.image)
                .placeholder(R.drawable.userprofile).into(binding.idImageView)


            if (chattingList[position].file != null && chattingList[position].file.isNotEmpty()) {
                Glide.with(requireContext).load(chattingList[position].file[0])
                    .placeholder(R.drawable.userprofile).into(binding.idReceiverImage)

                binding.idReceiverPlay.isVisible = item?.file?.get(0)?.endsWith(".mp4")!!
            }

            if (chattingList[position].messageType == 1) {
                binding.idMedia.isVisible = false
                binding.idReceiverMessage.isVisible = true
            } else if (chattingList[position].messageType == 6 || chattingList[position].messageType == 7) {
                binding.idSenderMedia.isVisible = false
                binding.idSenderMessage.isVisible = false
            } else {
                binding.idMedia.isVisible = true
                binding.idReceiverMessage.isVisible = false

            }
        } else {
            binding.idReceiverLayout.isVisible = false
            binding.idSenderLayout.isVisible = true
            binding.idSenderMessage.text = chattingList[position].message
            binding.idSenderTime.text =
                convertTimeStampToTime(chattingList[position].createdAt, 10, 0)
            if (chattingList[position].file != null && chattingList[position].file.isNotEmpty()) {
                Glide.with(requireContext).load(chattingList[position].file[0])
                    .placeholder(R.drawable.userprofile).into(binding.idSenderImage)
                binding.idButton.isVisible = item?.file?.get(0)?.endsWith(".mp4")!!
            }

            if (chattingList[position].messageType == 1) {
                binding.idSenderMedia.isVisible = false
                binding.idSenderMessage.isVisible = true
            } else if (chattingList[position].messageType == 6 || chattingList[position].messageType == 7) {
                binding.idSenderMedia.isVisible = false
                binding.idSenderMessage.isVisible = false
            } else {
                binding.idSenderMedia.isVisible = true
                binding.idSenderMessage.isVisible = false

            }


        }
    }


    fun updateList(chatList: java.util.ArrayList<ChatMessage>) {
        chattingList = chatList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return chattingList.size
    }


    /*
    * chat list me user ki list me seen by ka array aa rha hai agar seen by me meri id aati hai to seend kr rakkha hai nnahi to nahi kr raha
    * */
}