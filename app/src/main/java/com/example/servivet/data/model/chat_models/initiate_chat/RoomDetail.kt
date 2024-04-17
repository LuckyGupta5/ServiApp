package com.example.servivet.data.model.chat_models.initiate_chat

data class RoomDetail(
    val __v: Int,
    val _id: String,
    val blockUser: List<Any>,
    val createdAt: String,
    val deletedBy: List<Any>,
    val groupMember: List<Any>,
    val isAccepeted: Boolean,
    val isGroupChat: Boolean,
    val lastMessage: String,
    val messageType: Int,
    val receiverId: ReceiverId,
    val seenBy: List<Any>,
    val senderId: SenderId,
    val status: Int,
    val updatedAt: String
)