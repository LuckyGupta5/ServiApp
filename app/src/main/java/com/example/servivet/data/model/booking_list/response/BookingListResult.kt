package com.example.servivet.data.model.booking_list.response

data class BookingListResult(
    val myBookingList: ArrayList<MyBooking>,
    val mySoldBookingList: ArrayList<MyBooking>,
    val totalMyBooking: Int,
    val totalMySoldBooking: Int
)