package com.example.servivet.data.model.send_otp.response

data class SendOtpResult(
    val otp: Int,
    val otpOrderNumber: String,
    val time: Int
)