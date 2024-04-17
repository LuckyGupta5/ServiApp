package com.example.servivet.data.model.booking_module.wallte_transaction.response

data class Result(
    val total: Int,
    val walletTransaction: ArrayList<WalletTransaction>
)