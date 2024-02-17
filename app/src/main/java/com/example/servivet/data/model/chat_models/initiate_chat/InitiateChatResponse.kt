package com.example.servivet.data.model.chat_models.initiate_chat

data class InitiateChatResponse(
    val message: String,
    val result: Result,
    val status: Int
)