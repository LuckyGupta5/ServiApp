package com.example.servivet.data.model.sold_booking_list.response

data class SoldBookingListServiceDetail(
    val _id: String,
    val atCenterPrice: Int,
    val atHomePrice: Int,
    val bussinessType: Int,
    val createdBy: String,
    val subCategory: String,
    val images: List<String>,
    val serviceMode: SoldBookingListServiceMode,
    val serviceName: String
)