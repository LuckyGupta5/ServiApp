package com.example.servivet.data.model.chat_models.request_list.response

import com.example.servivet.utils.interfaces.ListAdapterItem
import java.io.Serializable

data class Result(
    val chatlist: List<Chatlist>
)