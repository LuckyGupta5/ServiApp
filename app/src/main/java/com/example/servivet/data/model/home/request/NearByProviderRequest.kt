package com.example.servivet.data.model.home.request

data class NearByProviderRequest(
    var latitude: Double? = null,
    var limit: Int? = null,
    var longitude: Double? = null,
    var page: Int? = null,
    var userId: String? = null
)