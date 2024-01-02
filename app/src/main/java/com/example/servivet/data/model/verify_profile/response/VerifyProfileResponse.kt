package com.example.servivet.data.model.verify_profile.response

data class VerifyProfileResponse(
    val code: Int,
    val message: String,
    val result: VerifyProfileResult
)