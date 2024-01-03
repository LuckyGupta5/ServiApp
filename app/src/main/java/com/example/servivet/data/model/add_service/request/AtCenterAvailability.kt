package com.example.servivet.data.model.add_service.request

import java.io.Serializable

data class AtCenterAvailability(
    var day: String?=null,
    var slot: ArrayList<ServiceListSlot>?=null,
    var isSelected:Boolean=false,
    var position: Int? = null,

):Serializable