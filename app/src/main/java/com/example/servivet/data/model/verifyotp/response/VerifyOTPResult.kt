package com.example.servivet.data.model.verifyotp.response

import com.example.servivet.data.model.current_api.response.DefaultAddress

data class VerifyOTPResult(
    val email: String,
    val isBusinessVerify: String,
    val isProfileVerify: Boolean,
    val name: String,
    val role: Int,
    val businessType: String? = null,
    val token: String,
    val defaultAddress: DefaultAddress? = null,
)