package com.example.servivet.data.model.booking_module.booking_summary.response

data class ServiceDetail(
    val __v: Int?=null,
    val _id: String?=null,
    val aboutService: String?=null,
    val address: String?=null,
    val atCenterAvailability: List<AtCenterAvailability>?=null,
    val atCenterPrice: String?=null,
    val atHomeAvailability: List<Any>?=null,
    val atHomePrice: String?=null,
    val avgRating: String?=null,
    val bussinessType: Int?=null,
    val category: String?=null,
    val createdAt: String?=null,
    val createdBy: CreatedBy?=null,
    val images: List<String>?=null,
    val serviceMode: ServiceMode?=null,
    val serviceName: String?=null,
    val status: Int?=null,
    val subCategory: String?=null,
    val updatedAt: String?=null,
    var serviceModeLocal: String? = null,
    var date: String? = null,
    var time: String? = null,
    var addressLocal: String? = null,
    var slotId: String? = null


    )