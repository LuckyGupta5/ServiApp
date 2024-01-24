package com.example.servivet.data.model.booking_module.provider_leave

data class ProviderLeaveRequest(
    var isStartService: Boolean?=null,
    var leaveEndDate: String?=null,
    var leaveStartDate: String?=null
)