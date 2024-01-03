package com.example.servivet.data.model.add_service.response

import com.example.servivet.data.model.add_service.response.AddServiceesult

data class AddServiceResponse(
    val code: Int,
    val message: String,
    val result: AddServiceesult
)