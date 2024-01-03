package com.example.servivet.data.model.verifyotp.response

data class VerifyOTPResult(
    val email: String,
    val isBusinessVerify: Int,
    val isProfileVerify: Boolean,
    val name: String,
    val role: Int,
    val token: String
)