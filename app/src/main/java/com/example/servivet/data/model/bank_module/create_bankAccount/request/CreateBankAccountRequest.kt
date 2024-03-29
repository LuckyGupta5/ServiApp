package com.example.servivet.data.model.bank_module.create_bankAccount.request

data class CreateBankAccountRequest(
    var account_name: String?=null,
    var account_number: String?=null,
    var account_type: String?=null,
    var bank_code: String?=null,
    var country_code: String?=null,
    var currency: String?=null,
    var document_number: String?=null,
    var document_type: String?=null,
    var name: String?=null,
    var type: String?=null
)