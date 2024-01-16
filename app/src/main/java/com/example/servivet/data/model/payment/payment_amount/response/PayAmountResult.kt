package com.example.servivet.data.model.payment.payment_amount.response

data class PayAmountResult(
    val couponCode: String?=null,
    val couponDiscount: String?=null,
    val payableAmount: String?=null,
    val saveApplyCouponId: String?=null,
    val serviceAmount: String?=null,
    val taxAmount: String?=null
)