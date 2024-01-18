package com.example.servivet.data.model.booking_module.wallte_transaction.response

data class WalletTransactionResult(
    val total: Int,
    val walletTransaction: List<WalletTransaction>
)