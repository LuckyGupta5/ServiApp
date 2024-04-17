package com.example.servivet.data.model.payment.payment_amount.response

data class PayAmountResult(
    val couponCode: String?=null,
    val couponDiscount: Float?=null,
    val payableAmount: Double?=null,
    val saveApplyCouponId: String?=null,
    val serviceAmount: Float?=null,
    val taxAmount: Float?=null
)