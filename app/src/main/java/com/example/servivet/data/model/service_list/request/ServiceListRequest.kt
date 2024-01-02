package com.example.servivet.data.model.service_list.request

data class ServiceListRequest(
    var bussinessType: Int?=null,
    var category: String?=null,
    var isMyService: Int?=null,
    var limit: Int?=null,
    var page: Int?=null,
    var search: String?=null,
    var subCategory: String?=null
)