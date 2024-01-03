package com.example.servivet.data.model.service_category_details.response

import com.example.servivet.data.model.add_service.request.AtCenterAvailability
import com.example.servivet.data.model.add_service.request.AtHomeAvailability
import java.io.Serializable

data class ServiceDetail(
    val __v: Int?=null,
    val _id: String?=null,
    val aboutService: String?=null,
    val address: String?=null,
    val atCenterAvailability: ArrayList<AtCenterAvailability>?=null,
    val atCenterPrice: Double?=null,
    val atHomeAvailability: ArrayList<AtHomeAvailability>?=null,
    val atHomePrice: Double?=null,
    val avgRating: String?=null,
    val bussinessType: Int?=null,
    val category: String?=null,
    val createdAt: String?=null,
    val createdBy: ServiceDetailsCreatedBy?=null,
    val images: ArrayList<String>?=null,
    val location: ServiceDetailsLocation?=null,
    val serviceMode: ServiceMode?=null,
    val serviceName: String?=null,
    val status: Int?=null,
    val subCategory: String?=null,
    val subCategoryDetail: SubCategoryDeatils?=null,
    val updatedAt: String?=null
):Serializable