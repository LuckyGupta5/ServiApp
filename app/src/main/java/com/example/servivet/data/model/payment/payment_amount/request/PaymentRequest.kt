package com.example.servivet.data.model.payment.payment_amount.request

data class PaymentRequest(
    var couponCode: String?=null,
    var isCouponApply: Boolean?=null,
    var serviceId: String?=null,
    var serviceMode: String?=null,
    var slotId: String?=null
)