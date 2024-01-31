package com.example.servivet.data.model.cancel_booking.request

data class CancelBookingRequest(
    var bookingId: String = "",
    var cancelledBy: String = "",
    var reason: String = "",
    var isWantRefundInWallet: Boolean = false
)