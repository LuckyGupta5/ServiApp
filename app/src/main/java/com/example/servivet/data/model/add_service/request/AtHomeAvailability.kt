package com.example.servivet.data.model.add_service.request

import com.example.servivet.data.model.booking_module.booking_summary.response.Slot
import java.io.Serializable

data class AtHomeAvailability(
    var day: String?=null,
    var slot: ArrayList<ServiceListSlot>?=null,
    var isSelected:Boolean=false,
    var position: Int? = null,
):Serializable