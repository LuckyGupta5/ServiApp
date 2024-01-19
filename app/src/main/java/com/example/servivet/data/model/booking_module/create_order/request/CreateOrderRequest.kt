package com.example.servivet.data.model.booking_module.create_order.request

data class CreateOrderRequest(
    var addressId: String?=null,
    var bookingDate: String?=null,
    var couponCode: String?=null,
    var couponDiscount: Float?=null,
    var day: String?=null,
    var endTime: String?=null,
    var isCouponApply: Boolean?=null,
    var isWalletAmountInclude: Boolean?=null,
    var payableAmount: Float?=null,
    var paymentMode: String?=null,
    var saveApplyCouponId: String?=null,
    var serviceAmount: Float?=null,
    var serviceId: String?=null,
    var serviceMode: String?=null,
    var slotId: String?=null,
    var startTime: String?=null,
    var taxAmount: Float?=null,
    var walletAmount: Float?=null
)