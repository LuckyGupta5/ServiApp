package com.example.servivet.data.model.home.response

import com.example.servivet.utils.interfaces.ListAdapterItem
import java.io.Serializable

data class HomeServiceCategory(
    val _id: String?=null,
    val id: String?=null,
    val image: String?=null,
    val imageUrl: String?=null,
    val name: String?=null,
    val subCategory: ArrayList<HomeSubCategory>?=null
):ListAdapterItem,Serializable