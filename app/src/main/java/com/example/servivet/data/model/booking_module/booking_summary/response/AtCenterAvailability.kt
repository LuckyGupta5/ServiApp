package com.example.servivet.data.model.booking_module.booking_summary.response

import com.example.servivet.data.model.add_service.request.ServiceListSlot
import com.example.servivet.utils.interfaces.ListAdapterItem
import java.io.Serializable

data class AtCenterAvailability(
    val _id: String,
    val day: String,
    val slot: List<Slot>
) : ListAdapterItem, Serializable