package com.example.servivet.data.model.booking_module.coupon.response

import com.example.servivet.utils.interfaces.ListAdapterItem
import java.io.Serializable

data class CouponList(
    val _id: String,
    val accountStatus: Int,
    val bearerBy: Int,
    val couponMaxAmount: Int,
    val minAmount: Int,
    val amount: Int,
    val couponCode: String,
    val description: String
):ListAdapterItem,Serializable