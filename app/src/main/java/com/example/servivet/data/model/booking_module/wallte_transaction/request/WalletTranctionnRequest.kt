package com.example.servivet.data.model.booking_module.wallte_transaction.request

data class WalletTranctionnRequest(
    var isBought: Boolean?=null,
    var limit: Int?=null,
    var page: Int?=null
)