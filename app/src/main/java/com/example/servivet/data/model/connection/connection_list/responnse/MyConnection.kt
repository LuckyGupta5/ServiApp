package com.example.servivet.data.model.connection.connection_list.responnse

import com.example.servivet.utils.interfaces.ListAdapterItem
import java.io.Serializable

data class MyConnection(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val friendId: String,
    val status: Int,
    val businessType: Int,
    val updatedAt: String,
    val user: String,
    val userDetail: UserDetail
):Serializable,ListAdapterItem