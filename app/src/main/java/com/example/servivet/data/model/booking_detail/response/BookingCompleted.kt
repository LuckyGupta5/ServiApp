package com.example.servivet.data.model.booking_detail.response

data class BookingCompleted(
    val _id: String,
    val admin: Admin,
    val consumer: Consumer,
    val provider: Provider?=null
)