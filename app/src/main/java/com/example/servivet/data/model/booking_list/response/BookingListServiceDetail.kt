package com.example.servivet.data.model.booking_list.response

data class BookingListServiceDetail(
    val _id: String,
    val atCenterPrice: Int,
    val atHomePrice: Int,
    val bussinessType: Int,
    val createdBy: String,
    val subCategory: String,
    val images: List<String>,
    val serviceMode: BookingListServiceMode,
    val serviceName: String
)