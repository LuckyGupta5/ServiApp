package com.example.servivet.data.model.service_list

import com.example.servivet.data.model.service_list.response.ServiceListResult

data class ServiceListResponse(
    val code: Int,
    val message: String,
    val result: ServiceListResult
)