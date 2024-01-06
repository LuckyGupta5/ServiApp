package com.example.servivet.data.model.review_ratinng

import com.example.servivet.utils.interfaces.ListAdapterItem
import java.io.Serializable

data class ReviewRating(
    val _id: String,
    val comment: String,
    val createdAt: String,
    val rating: Float,
    val user: User
): ListAdapterItem, Serializable