package com.example.servivet.data.model.service_list.response

import java.io.Serializable

data class ServiceListLocation(
    val coordinates: List<String>,
    val type: String
): Serializable