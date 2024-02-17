package com.example.servivet.data.model.chat_models.chat_list

import com.example.servivet.utils.interfaces.ListAdapterItem
import java.io.Serializable

data class ChatMessage(
    val __v: Int,
    val _id: String,
    val blockUser: List<Any>,
    var createdAt: String,
    val deletedBy: List<Any>,
    val `file`: ArrayList<String>,
    val message: String,
    val messageType: Int,
    val roomId: String,
    val seenBy: List<String>,
    val senderId: SenderId,
    val status: Int,
    val updatedAt: String,
    var containMP4:Boolean
):Serializable,ListAdapterItem