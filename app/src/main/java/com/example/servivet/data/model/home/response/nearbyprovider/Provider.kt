package com.example.servivet.data.model.home.response.nearbyprovider

import com.example.servivet.utils.interfaces.ListAdapterItem
import java.io.Serializable

data class Provider(
    val _id: String,
    val avgRating: String,
    val businessType: Int,
    val category: List<Category>,
    var distance: String,
    val image: String,
    val name: String
):Serializable, ListAdapterItem