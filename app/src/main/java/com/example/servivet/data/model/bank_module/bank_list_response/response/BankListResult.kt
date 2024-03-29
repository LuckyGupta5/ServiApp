package com.example.servivet.data.model.bank_module.bank_list_response.response

data class BankListResult(
    val bank: List<Bank>?=null,
    val total: Int?=null,
    /*local variable*/
    var bankName:String?=null
)