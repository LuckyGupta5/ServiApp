package com.example.servivet.data.model.booking_list.response

import com.example.servivet.utils.interfaces.ListAdapterItem

data class MyBooking(
    val __v: Int,
    val _id: String,
    val address: String,
    val adminCommision: Double,
    val baseAmount: Int,
    val bookedFor: String,
    val bookingDate: String,
    val bookingId: String,
    val coupon: BookingListCoupon,
    val createdAt: String,
    val createdBy: BookingListCreatedBy,
    val day: String,
    val endTime: String,
    val invoiceNo: String,
    val paidAmount: String,
    val serviceDetail: BookingListServiceDetail,
    val serviceId: String,
    val serviceStatus: Int,
    val slotId: String,
    val startTime: String,
    val status: Int,
    val taxAmount: Double,
    val transactionId: String,
    val updatedAt: String,
    val user: String,
    val userDetail: SoldBookingUserDetail,

    // local Addition
    var type:String?=null
):ListAdapterItem