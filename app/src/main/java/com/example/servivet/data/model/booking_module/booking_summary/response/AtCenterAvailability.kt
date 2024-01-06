package com.example.servivet.data.model.booking_module.booking_summary.response

data class AtCenterAvailability(
    val _id: String,
    val day: String,
    val slot: List<Slot>
)