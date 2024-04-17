package com.example.servivet.data.model.chat_models.chat_list

data class ChattingListResponse(
    val message: String,
    val result: ChatListResult,
    val status: Int
)