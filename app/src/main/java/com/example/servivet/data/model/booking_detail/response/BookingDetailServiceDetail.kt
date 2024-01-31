package com.example.servivet.data.model.booking_detail.response

data class BookingDetailServiceDetail(
    val _id: String,
    val atCenterPrice: String,
    val atHomePrice: String,
    val bussinessType: Int,
    val createdBy: String,
    val subCategory: String,
    val images: List<String>,
    val serviceMode: BookingDeatilServiceMode,
    val serviceName: String
)