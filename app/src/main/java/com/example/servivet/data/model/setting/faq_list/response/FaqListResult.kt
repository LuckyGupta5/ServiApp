package com.example.servivet.data.model.setting.faq_list.response

import com.example.servivet.utils.interfaces.ListAdapterItem

data class FaqListResult(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val desc: String,
    val faqTypeId: String,
    val faq_data: FaqListData,
    val status: Int,
    val title: String,
    val updatedAt: String,
    val userType: Int
):ListAdapterItem