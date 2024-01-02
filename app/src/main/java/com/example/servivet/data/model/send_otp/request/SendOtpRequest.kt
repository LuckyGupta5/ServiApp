package com.example.servivet.data.model.send_otp.request

data class SendOtpRequest(
    var countryCode: String?=null,
    var mobile: String?=null
)