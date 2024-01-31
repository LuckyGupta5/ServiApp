package com.example.servivet.data.model.booking_module.wallte_transaction.response

import com.example.servivet.utils.interfaces.ListAdapterItem

data class WalletTransaction(
    val _id: String,
    val amount: String,
    val paymentType: String,
    val service: Service,
    val tranactionDate: String,
    val transactionId: String
): ListAdapterItem