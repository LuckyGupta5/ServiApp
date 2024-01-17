package com.example.servivet.data.model.sold_booking_list.request

data class SoldBookingListRequest(
    val limit: Int,
    val myBookingStatus: Int,
    val page: Int
)