package com.example.servivet.data.model.common.request

import com.google.gson.annotations.SerializedName

data class CommonRequest(
    @SerializedName("servivet_user_req")
    var servivet_user_req: String? = null
)