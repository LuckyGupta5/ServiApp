package com.example.servivet.data.model.edit_profile.request

data class EditProfileRequest(
    var aboutus: String? = null,
    var coverImage: String? = null,
    var email: String? = null,
    var image: String? = null,
    var mobile: String? = null,
    var name: String? = null,
    var countryCode: String?=null
    )