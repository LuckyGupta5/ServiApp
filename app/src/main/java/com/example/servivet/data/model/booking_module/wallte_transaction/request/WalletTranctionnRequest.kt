package com.example.servivet.data.model.booking_module.wallte_transaction.request

data class WalletTranctionnRequest(
    val isBought: Boolean?=null,
    val limit: Int?=null,
    val page: Int?=null
)