package com.example.servivet.data.model.service_category_details.response

import java.io.Serializable

data class ServiceDetailsLocation(
    val coordinates: List<Double>?=null,
    val type: String?=null
): Serializable