package com.example.servivet.data.model.verify_profile.request

data class VerifyProfileRequest(
    val businessType: String,
    val documentNumber: String,
    val documentType: String,
    val email: String,
    val name: String,
    val nameOnDocument: String,
    val userType: Int
)