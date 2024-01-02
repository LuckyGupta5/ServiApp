package com.example.servivet.data.model.verifyotp.request

data class VerifyOtpRequest(
    var countryCode: String?=null,
    var deviceId: String?=null,
    var deviceModelNo: String?=null,
    var deviceToken: String?=null,
    var deviceType: String?=null,
    var deviceVersion: String?=null,
    var mobile: String?=null,
    var otp: String?=null,
    var otpOrderNumber: String?=null,
    var userType: Int?=null
)