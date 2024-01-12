package com.example.servivet.data.model.payment.payment_amount.response

data class PaymentResponseMain(
    val code: Int,
    val message: String,
    val result: PayAmountResult
)