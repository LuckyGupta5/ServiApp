package com.example.servivet.data.model.chat_models.request_list.response

import com.example.servivet.utils.interfaces.ListAdapterItem
import java.io.Serializable

data class Chatlist(
    val __v: Int,
    val _id: String,
    val blockUser: List<Any>,
    var createdAt: String,
    val groupMember: List<Any>,
    val deletedBy: List<String>,
    val isAccepeted: Boolean,
    val isGroupChat: Boolean,
    val lastMessage: String,
    val messageType: Int,
    val receiverId: ReceiverId,
    val senderId: SenderId,
    val seenBy:ArrayList<String>,
    val status: Int,
    val updatedAt: String,
    var isSeenBy:Boolean
): Serializable, ListAdapterItem