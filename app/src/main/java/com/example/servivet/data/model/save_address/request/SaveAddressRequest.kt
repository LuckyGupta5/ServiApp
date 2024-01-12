package com.example.servivet.data.model.save_address.request

data class SaveAddressRequest(
    var addressActionType: String?=null,
    var addressId: String?=null,
    var city: String?=null,
    var country: String?=null,
    var fullAddress: String?=null,
    var latitute: String?=null,
    var longitute: String?=null,
    var mobileNumber: String?=null,
    var name: String?=null,
    var postalCode: String?=null,
    val state: String?=null
)