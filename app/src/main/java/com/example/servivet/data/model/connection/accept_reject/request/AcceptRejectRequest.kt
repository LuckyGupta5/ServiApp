package com.example.servivet.data.model.connection.accept_reject.request

data class AcceptRejectRequest(
    var connectionId: String? = null,
    var isAccept: Int? = null
)