package com.example.servivet.data.model.connection.connection_request.request

data class ConnectionRequest(
    var connectionId: String?=null,
    var connectionType: Int?=null,
    var friendId: String?=null
)