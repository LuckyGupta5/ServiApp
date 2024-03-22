package com.example.servivet.data.model.bank_module.bank_list_response.response

data class Bank(
    val active: Boolean,
    val code: String,
    val country: String,
    val createdAt: String,
    val currency: String,
    val gateway: String,
    val id: Int,
    val is_deleted: Boolean,
    val longcode: String,
    val name: String,
    val pay_with_bank: Boolean,
    val slug: String,
    val supports_transfer: Boolean,
    val type: String,
    val updatedAt: String
)