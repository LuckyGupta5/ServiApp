package com.example.servivet.data.model.setting.address_list

import java.io.Serializable

data class SettingLocation(
    val coordinates: List<Double>,
    val type: String
):Serializable