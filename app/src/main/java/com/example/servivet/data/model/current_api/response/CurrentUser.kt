package com.example.servivet.data.model.current_api.response

data class CurrentUser(
    val __v: Int,
    val _id: String,
    val activeSubscription: List<Any>,
    val businessDetail: BusinessDetail,
    val countryCode: String,
    val coverImage: String,
    val createdAt: String,
    val defaultAddress: DefaultAddress,
    val description: String,
    val email: String,
    val image: String,
    val isBusinessVerify: String,
    val isNotification: Boolean,
    val isProfileVerify: Boolean,
    val lastLogin: Any,
    var businessType:String,
    val mobile: String,
    val name: String,
    val profileVerifyDetail: ProfileVerifyDetail,
    val role: Int,
    val sockectActiveStatus: SockectActiveStatus,
    val status: Int,
    val updatedAt: String,

)