package com.example.servivet.data.model.business_verification_api.request

data class BusinessVerificationRequest(
    var businessType: String?=null,
    val documentNumber: String?=null,
    val documentType: String?=null,
    var email: String?=null,
    var name: String?=null,
    val nameOnDocument: String?= null,
    var userType: Int?=null
)