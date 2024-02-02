package com.example.servivet.data.model.user_profile.response

data class UserProfile(
    val _id: String,
    val coverImage: String,
    val description: String,
    val image: String,
    val isConnected: Int,
    val myContact: List<Any>,
    val name: String,
    val email: String,
    val mobile: String,
    val countryCode: String,
    val role: Int,
    val businessType: Int
)