package com.example.servivet.data.model.call_module.notification_call

data class CallBody(
    val callStatus: String,
    val callToken: String,
    val channelName: String,
    val chatMessageId: String,
    val duration: Int,
    val `file`: List<Any>,
    val message: String,
    val messageType: Int,
    val receiverId: String,
    val roomId: String,
    val senderId: SenderId,
    val userType: String
)