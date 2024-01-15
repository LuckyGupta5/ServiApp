package com.example.servivet.data.model.cancel_booking.response

data class CancelBookingResponse(
    val code: Int,
    val message: String,
    val result: CancelBookingResult
)