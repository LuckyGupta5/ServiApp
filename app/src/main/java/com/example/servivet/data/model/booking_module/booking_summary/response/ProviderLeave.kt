package com.example.servivet.data.model.booking_module.booking_summary.response

data class ProviderLeave(
    val _id: String,
    val businessProviderId: String,
    val category: Any,
    val leaveEndDate: String,
    val leaveStartDate: String,
    val serviceId: Any,
    val status: Int,
    val subCategory: Any
)