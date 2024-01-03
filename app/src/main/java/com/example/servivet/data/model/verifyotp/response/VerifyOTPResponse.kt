package com.example.servivet.data.model.verifyotp.response

data class VerifyOTPResponse(
    val code: Int,
    val message: String,
    val result: VerifyOTPResult
)