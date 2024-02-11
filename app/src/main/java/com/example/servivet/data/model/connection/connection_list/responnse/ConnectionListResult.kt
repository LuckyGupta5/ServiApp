package com.example.servivet.data.model.connection.connection_list.responnse

data class ConnectionListResult(
    val myConnectionList: List<MyConnection>,
    val connectionRequestList: List<MyConnection>,
    val total: Int
)