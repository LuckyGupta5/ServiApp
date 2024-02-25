package com.example.servivet.data.model.setting.faq_list.response

data class FaqListResponse(
    val code: Int,
    val message: String,
    val result: ArrayList<FaqListResult>
)