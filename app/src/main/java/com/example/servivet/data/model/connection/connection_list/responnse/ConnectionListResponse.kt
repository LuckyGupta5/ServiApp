package com.example.servivet.data.model.connection.connection_list.responnse

data class ConnectionListResponse(
    val code: Int,
    val message: String,
    val result: ConnectionListResult
)