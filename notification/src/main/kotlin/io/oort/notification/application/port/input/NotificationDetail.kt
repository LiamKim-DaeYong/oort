package io.oort.notification.application.port.input

import io.oort.notification.domain.NotificationChannel
import io.oort.notification.domain.NotificationStatus
import java.time.Instant
import java.util.UUID

data class NotificationDetail(
    val id: UUID,
    val channel: NotificationChannel,
    val recipient: String,
    val title: String,
    val content: String,
    val status: NotificationStatus,
    val requestedAt: Instant,
    val completedAt: Instant?,
)
