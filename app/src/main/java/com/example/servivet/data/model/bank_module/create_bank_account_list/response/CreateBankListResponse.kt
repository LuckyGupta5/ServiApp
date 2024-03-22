package com.example.servivet.data.model.bank_module.create_bank_account_list.response

data class CreateBankListResponse(
    val code: Int,
    val message: String,
    val result: CreateBankResult
)