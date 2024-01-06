package com.example.servivet.data.model.booking_module.coupon.response

data class Coupon(
    val _id: String,
    val accountStatus: Int,
    val bearerBy: Int,
    val couponCode: String,
    val description: String
)