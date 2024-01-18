package com.example.servivet.data.model.booking_module.wallte_transaction.response

data class WalletTransaction(
    val _id: String,
    val amount: Double,
    val paymentType: String,
    val service: WalletService,
    val tranactionDate: String,
    val transactionId: String
)