package com.example.servivet.data.model.setting.faq_type_list.response

data class FaqTypeListResponse(
    val code: Int,
    val message: String,
    val result: ArrayList<FaqTypeListResult>
)