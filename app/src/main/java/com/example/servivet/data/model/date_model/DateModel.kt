package com.example.servivet.data.model.date_model

import com.example.servivet.utils.interfaces.ListAdapterItem
import java.util.Date

data class DateModel(
    var date: String? = null,
    var isToday: Boolean = false):ListAdapterItem