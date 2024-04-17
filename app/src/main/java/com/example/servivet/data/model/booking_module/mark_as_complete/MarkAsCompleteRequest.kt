package com.example.servivet.data.model.booking_module.mark_as_complete

data class MarkAsCompleteRequest(
    var bookingId: String?=null,
    var completedBy: String?=null
)