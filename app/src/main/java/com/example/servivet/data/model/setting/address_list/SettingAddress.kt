package com.example.servivet.data.model.setting.address_list

import com.example.servivet.utils.interfaces.ListAdapterItem
import java.io.Serializable

data class SettingAddress(
    val __v: Int?=null,
    val _id: String?=null,
    val city: String?=null,
    val fullAddress: String?=null,
    val isDefault: Boolean?=null,
    val location: SettingLocation?=null,
    val mobileNumber: Long?=null,
    val name: String?=null,
    val postalCode: Int?=null,
    val state: String?=null,
    val status: Int?=null,
    val user: String?=null
):ListAdapterItem,Serializable