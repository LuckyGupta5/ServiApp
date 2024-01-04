package com.example.servivet.data.model.booking_model.request

data class RatingRequest(
    var comment: String?=null,
    var rating: Int?=null,
    var serviceId: String?=null
)