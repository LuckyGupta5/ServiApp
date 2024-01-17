package com.example.servivet.data.model.booking_list.request

data class BookingListRequest(
    val limit: Int,
    val myBookingStatus: Int,
    val page: Int
)