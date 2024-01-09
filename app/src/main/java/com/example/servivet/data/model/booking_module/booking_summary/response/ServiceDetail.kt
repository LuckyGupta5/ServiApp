package com.example.servivet.data.model.booking_module.booking_summary.response

data class ServiceDetail(
    val __v: Int,
    val _id: String,
    val aboutService: String,
    val address: String,
    val atCenterAvailability: List<AtCenterAvailability>,
    val atCenterPrice: Int,
    val atHomeAvailability: List<Any>,
    val atHomePrice: Int,
    val avgRating: String,
    val bussinessType: Int,
    val category: String,
    val createdAt: String,
    val createdBy: CreatedBy,
    val images: List<String>,
    val serviceMode: ServiceMode,
    val serviceName: String,
    val status: Int,
    val subCategory: String,
    val updatedAt: String
)