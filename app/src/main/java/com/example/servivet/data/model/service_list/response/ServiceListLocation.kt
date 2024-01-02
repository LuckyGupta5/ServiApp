package com.example.servivet.data.model.service_list.response

import java.io.Serializable

data class ServiceListLocation(
    val coordinates: List<Double>,
    val type: String
): Serializable