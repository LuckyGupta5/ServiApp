package com.example.servivet.data.model.home.response

import com.example.servivet.utils.interfaces.ListAdapterItem
import java.io.Serializable

data class HomeSubCategory(
    val _id: String?=null,
    val createdAt: String?=null,
    val id: String?=null,
    val image: String?=null,
    val name: String?=null,
    val status: Int?=null
):ListAdapterItem,Serializable