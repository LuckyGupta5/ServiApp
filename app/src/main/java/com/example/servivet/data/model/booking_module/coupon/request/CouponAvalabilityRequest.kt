package com.example.servivet.data.model.booking_module.coupon.request

data class CouponAvalabilityRequest(
    var bookingDate: String?=null,
    var serviceId: String?=null,
    var serviceMode: String?=null,
    var slotId: String?=null
)