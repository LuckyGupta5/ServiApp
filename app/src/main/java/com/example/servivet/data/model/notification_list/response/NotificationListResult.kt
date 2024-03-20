package com.example.servivet.data.model.notification_list.response

data class NotificationListResult(
    val `data`: List<NotificationList>,
    val total: Int
)