package com.example.servivet.data.model.booking_module.create_order.request

data class CreateOrderRequest(
    var addressId: String?=null,
    var bookingDate: String?=null,
    var couponCode: String?=null,
    var couponDiscount: String?=null,
    var day: String?=null,
    var endTime: String?=null,
    var isCouponApply: Boolean?=null,
    var isWalletAmountInclude: String?=null,
    var payableAmount: String?=null,
    var paymentMode: String?=null,
    var saveApplyCouponId: String?=null,
    var serviceAmount: String?=null,
    var serviceId: String?=null,
    var serviceMode: String?=null,
    var slotId: String?=null,
    var startTime: String?=null,
    var taxAmount: String?=null,
    var walletAmount: String?=null
)