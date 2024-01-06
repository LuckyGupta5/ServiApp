package com.example.servivet.data.model.booking_module.booking_slot

data class BookedSlot(
    val _id: String,
    val bookingDate: String,
    val endTime: String,
    val slotId: String,
    val startTime: String
)