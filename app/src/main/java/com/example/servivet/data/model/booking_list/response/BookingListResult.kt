package com.example.servivet.data.model.booking_list.response

data class BookingListResult(
    val myBookingList: ArrayList<MyBooking>,
    val totalMyBooking: Int
)