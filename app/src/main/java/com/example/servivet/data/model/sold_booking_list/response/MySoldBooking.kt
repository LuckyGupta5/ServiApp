package com.example.servivet.data.model.sold_booking_list.response

import com.example.servivet.utils.interfaces.ListAdapterItem

data class MySoldBooking(
    val __v: Int,
    val _id: String,
    val address: String,
    val adminCommision: Double,
    val baseAmount: Int,
    val bookedFor: String,
    val bookingDate: String,
    val bookingId: String,
    val bookingStatus: Int,
    val coupon: SoldBookingCoupon,
    val createdAt: String,
    val day: String,
    val endTime: String,
    val invoiceNo: String,
    val paidAmount: Double,
    val serviceDetail: SoldBookingListServiceDetail,
    val serviceId: String,
    val serviceStatus: Int,
    val slotId: String,
    val startTime: String,
    val status: Int,
    val taxAmount: Double,
    val transactionId: String,
    val updatedAt: String,
    val user: SoldBookingListUser
):ListAdapterItem