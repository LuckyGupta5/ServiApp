package com.example.servivet.data.model.booking_module.reschedule_booking.request

data class RescheduleBookingRequest(
    var bookingDate: String? = null,
    var bookingTransactionId: String? = null,
    var day: String? = null,
    var endTime: String? = null,
    var slotId: String? = null,
    var startTime: String? = null
)