package com.example.servivet.data.model.common.response

data class CommonResponse(
    val code: Int?=null,
    val message: String?=null,
    val welcome: String?=null,
    val result: Result? = null
)