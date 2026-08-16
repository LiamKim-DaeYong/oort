package io.oort.notification.application.port.input.create

import io.oort.notification.domain.NotificationChannel

data class CreateNotificationCommand(
    val channel: NotificationChannel,
    val recipient: String,
    val title: String,
    val content: String,
)
