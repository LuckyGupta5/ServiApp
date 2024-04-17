package com.example.servivet.data.model.notification_list.response

data class NotificationList(
    val __v: Int,
    val _id: String,
    val admin: Any,
    val createdAt: String,
    val description: String,
    val isSeen: Boolean,
    val notificationFrom: String,
    val notificationType: String,
    val `receiver`: String,
    val sender: String,
    val subject: String,
    val title: String,
    val updatedAt: String
)