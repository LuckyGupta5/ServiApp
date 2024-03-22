package com.example.servivet.data.model.bank_module.create_bank_account_list.response

data class UserBank(
    val __v: Int,
    val _id: String,
    val accountNumber: String,
    val bankCode: String,
    val bankName: String,
    val bankUserName: String,
    val country: String,
    val createdAt: String,
    val currency: String,
    val recipientCode: String,
    val status: Int,
    val type: String,
    val updatedAt: String,
    val user: String
)