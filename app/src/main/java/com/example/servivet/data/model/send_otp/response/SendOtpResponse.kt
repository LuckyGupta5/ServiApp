package com.example.servivet.data.model.send_otp.response

data class SendOtpResponse(
    val code: Int,
    val message: String,
    val result: SendOtpResult
)