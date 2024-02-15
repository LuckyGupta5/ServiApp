package com.example.servivet.data.model.chat_models.request_list.response

import com.example.servivet.utils.interfaces.ListAdapterItem
import java.io.Serializable

data class Chatlist(
    val __v: Int,
    val _id: String,
    val blockUser: List<Any>,
    val createdAt: String,
    val groupMember: List<Any>,
    val isAccepeted: Boolean,
    val isGroupChat: Boolean,
    val lastMessage: String,
    val messageType: Int,
    val receiverId: ReceiverId,
    val senderId: SenderId,
    val status: Int,
    val updatedAt: String
): Serializable, ListAdapterItem