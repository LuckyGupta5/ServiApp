package com.example.servivet.data.model.booking_module.booking_summary.request

data class ScheduleRequest(
    var date: String? = null,
    var time: String? = null,
    var serviceMode: String? = null,
    val address: String? = null
    )