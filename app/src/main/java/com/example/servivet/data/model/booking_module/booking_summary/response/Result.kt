package com.example.servivet.data.model.booking_module.booking_summary.response

data class Result(
    val providerLeaveList: List<ProviderLeave>?=null,
    val serviceDetail: ServiceDetail?=null,
    var scheduleRequest: ScheduleRequest?=null
)