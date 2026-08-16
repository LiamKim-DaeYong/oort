package io.oort.notification.adapter.input.web.dto

import io.oort.notification.application.port.input.NotificationDetail
import io.oort.notification.domain.NotificationChannel
import io.oort.notification.domain.NotificationStatus
import java.time.Instant
import java.util.UUID

data class NotificationResponse(
    val id: UUID,
    val channel: NotificationChannel,
    val recipient: String,
    val title: String,
    val content: String,
    val status: NotificationStatus,
    val requestedAt: Instant,
    val completedAt: Instant?,
)

internal fun NotificationDetail.toResponse(): NotificationResponse =
    NotificationResponse(
        id = id,
        channel = channel,
        recipient = recipient,
        title = title,
        content = content,
        status = status,
        requestedAt = requestedAt,
        completedAt = completedAt,
    )
