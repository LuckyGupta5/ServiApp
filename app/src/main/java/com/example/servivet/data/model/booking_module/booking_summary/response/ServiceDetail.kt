package com.example.servivet.data.model.booking_module.booking_summary.response

import com.example.servivet.data.model.payment.payment_amount.response.PayAmountResult
import java.io.Serializable

data class ServiceDetail(
    val __v: Int?=null,
    val _id: String?=null,
    val aboutService: String?=null,
    val address: String?=null,
    val atCenterAvailability: List<AtCenterAvailability>?=null,
    val atCenterPrice: String?=null,
    val atHomeAvailability: List<AtHomeAvailability>?=null,
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

    //AddedData
    var serviceModeLocal: String? = null,
    var date: String? = null,
    var day: String? = null,
    var startTime: String? = null,
    var endTime: String? = null,
    var addressLocal: String? = null,
    var slotId: String? = null,
    var couponCode:String =""

):Serializable