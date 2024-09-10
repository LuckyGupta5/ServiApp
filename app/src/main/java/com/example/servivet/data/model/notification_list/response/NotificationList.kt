package com.example.servivet.data.model.notification_list.response

import com.example.servivet.utils.CommonUtils.formatDate
import com.example.servivet.utils.CommonUtils.sendMessageTime

data class NotificationList(
    val __v: Int,
    val _id: String,
    val admin: Any,
    val createdAt: String?=null,
    val description: String,
    val isSeen: Boolean,
    val notificationFrom: String,
    val notificationType: String,
    val `receiver`: String,
    val sender: String,
    val subject: String,
    val title: String,
    val updatedAt: String
) {
    fun formattedDate(): String {
        return formatDate(createdAt ?: "")
    }

    fun sendTime(): String? {
        return sendMessageTime(createdAt ?: "")
    }
}