package com.example.servivet.data.model.service_list.response

import com.example.servivet.data.model.add_service.request.AtCenterAvailability
import com.example.servivet.data.model.add_service.request.AtHomeAvailability
import com.example.servivet.data.model.service_category_details.response.SubCategoryDeatils
import com.example.servivet.utils.interfaces.ListAdapterItem
import java.io.Serializable

data class ServiceList(
    val _id: String?=null,
    val aboutService: String?=null,
    val address: String?=null,
    val atCenterAvailability: List<AtCenterAvailability>?=null,
    val atCenterPrice: Double?=null,
    val atHomeAvailability: List<AtHomeAvailability>?=null,
    val atHomePrice: Double?=null,
    val avgRating: String?=null,
    val bussinessType: Int?=null,
    val category: String?=null,
    val createdBy: ServiceListCreatedBy?=null,
    val images: ArrayList<String>?=null,
    val location: ServiceListLocation?=null,
    val subCategoryDetail: SubCategoryDeatils?=null,
    val onlinePrice: Double?=null,
    val serviceMode: ServiceListMode?=null,
    val serviceName: String?=null,
    val status: String?=null,
    val subCategory: String?=null
): ListAdapterItem, Serializable