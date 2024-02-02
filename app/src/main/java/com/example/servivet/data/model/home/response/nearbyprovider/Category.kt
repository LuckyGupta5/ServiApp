package com.example.servivet.data.model.home.response.nearbyprovider

import com.example.servivet.utils.interfaces.ListAdapterItem
import java.io.Serializable

data class Category(
    val _id: String,
    val name: String
):Serializable,ListAdapterItem