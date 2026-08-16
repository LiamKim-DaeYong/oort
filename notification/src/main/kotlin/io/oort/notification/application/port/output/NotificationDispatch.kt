package io.oort.notification.application.port.output

import io.oort.notification.domain.NotificationChannel

data class NotificationDispatch(
    val channel: NotificationChannel,
    val recipient: String,
    val title: String,
    val content: String,
)
