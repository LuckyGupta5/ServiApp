package com.example.servivet.data.model.sold_booking_list.response

data class SoldBookingResult(
    val mySoldBookingList: ArrayList<MySoldBooking>,
    val totalMySoldBooking: Int
)