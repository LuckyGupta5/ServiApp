package com.example.servivet.data.model.add_service.request


data class AddServiceRequest(
    var atCenter: Boolean? = null,
    var atHome: Boolean? = null,
    var online: Boolean? = null,
    var serviceName: String? = null,
    var category: String? = null,
    var subCategory: String? = null,
    var aboutService: String? = null,
    var atCenterPrice: String? = "0",
    var atHomePrice: String? = "0",
    var onlinePrice: String? ="0",
    var address: String? = null,
    var latitute: String? = null,
    var longitute: String? = null,
    var serviceId: String? = null,
    var image: ArrayList<String> =ArrayList(),
    var deleteImage: ArrayList<String>? = null,
    var atCenterAvailability: ArrayList<AtCenterAvailability>? = null,
    var atHomeAvailability: ArrayList<AtHomeAvailability>? = null
)
