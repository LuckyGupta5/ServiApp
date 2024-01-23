package com.example.servivet.data.model.booking_detail.response

data class BookingDetail(
    val __v: Int,
    val _id: String,
    val address: String,
    val adminCommision: Double,
    val baseAmount: String,
    val bookedFor: String,
    val bookingDate: String,
    val bookingId: String,
    val coupon: BookingDetailCoupon,
    val createdAt: String,
    val day: String,
    val endTime: String,
    val invoiceNo: String,
    val paidAmount: String,
    val serviceDetail: BookingDetailServiceDetail,
    val serviceId: String,
    val serviceStatus: Int,
    val slotId: String,
    val startTime: String,
    val status: Int,
    val taxAmount: String,
    val transactionId: String,
    val updatedAt: String,
    val user: String,
    val userDetail: BookingDetailUserDetail
)