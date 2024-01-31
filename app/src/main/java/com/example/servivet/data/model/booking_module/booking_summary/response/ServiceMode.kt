package com.example.servivet.data.model.booking_module.booking_summary.response

import java.io.Serializable

data class ServiceMode(
    val atCenter: Boolean,
    val atHome: Boolean
):Serializable