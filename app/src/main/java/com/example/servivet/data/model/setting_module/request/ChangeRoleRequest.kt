package com.example.servivet.data.model.setting_module.request

data class ChangeRoleRequest(
    var businessType: Int? = null,
    var roleType: Int? = null
)