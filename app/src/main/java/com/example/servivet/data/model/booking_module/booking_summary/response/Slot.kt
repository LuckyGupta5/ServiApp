package com.example.servivet.data.model.booking_module.booking_summary.response

import com.example.servivet.utils.interfaces.ListAdapterItem
import java.io.Serializable

data class Slot(
    val _id: String,
    val endTime: String,
    val numOfSlot: Int,
    val remainingSlot: String,
    val startTime: String
): ListAdapterItem, Serializable