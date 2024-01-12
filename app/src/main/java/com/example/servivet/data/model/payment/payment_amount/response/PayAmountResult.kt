package com.example.servivet.data.model.payment.payment_amount.response

data class PayAmountResult(
    val couponCode: String,
    val couponDiscount: String,
    val payableAmount: String,
    val saveApplyCouponId: String,
    val serviceAmount: String,
    val taxAmount: String
)