package com.example.servivet.data.model.service_category_details.response

import java.io.Serializable

data class RatingReviews (
    val totalReview: String?=null,
    val averageRating: Float?=null,
    val five: Int?=null,
    val four: Int?=null,
    val three: Int?=null,
    val two: Int?=null,
    val one: Int?=null,

    ): Serializable

