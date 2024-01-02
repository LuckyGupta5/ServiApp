package com.example.servivet.data.model.service_list.response

import com.example.servivet.utils.interfaces.ListAdapterItem

data class ServiceListSlot(
//    var _id: String="",
    var endTime: String?=null,
    var numOfSlot: String?=null,
    var startTime: String?=null
): ListAdapterItem