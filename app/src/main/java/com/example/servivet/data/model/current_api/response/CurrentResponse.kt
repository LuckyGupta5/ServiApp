package com.example.servivet.data.model.current_api.response

data class CurrentResponse(
    val code: Int,
    val message: String,
    val result: CurrentResult
)