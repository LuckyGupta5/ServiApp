package com.example.servivet.data.model.add_service.request

import com.example.servivet.utils.interfaces.ListAdapterItem
import java.io.Serializable

data class ServiceListSlot(
    //var _id: String="",
    var endTime: String?=null,
    var numOfSlot: String?=null,
    var startTime: String?=null
): ListAdapterItem, Serializable