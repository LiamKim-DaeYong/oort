package io.oort.notification.application.port.input

import io.oort.notification.domain.Notification
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

internal fun Notification.toDetail(): NotificationDetail =
    NotificationDetail(
        id = id,
        channel = channel,
        recipient = recipient,
        title = title,
        content = content,
        status = status,
        requestedAt = requestedAt,
        completedAt = completedAt,
    )
