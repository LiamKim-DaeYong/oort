package io.oort.notification.application.port.input

import io.oort.notification.domain.NotificationChannel
import io.oort.notification.domain.NotificationStatus
import java.time.Instant
import java.util.UUID

interface NotificationUseCase {
    fun create(command: CreateNotificationCommand): NotificationResult

    fun get(notificationId: UUID): NotificationResult
}

data class CreateNotificationCommand(
    val channel: NotificationChannel,
    val recipient: String,
    val title: String,
    val content: String,
)

data class NotificationResult(
    val id: UUID,
    val channel: NotificationChannel,
    val recipient: String,
    val title: String,
    val content: String,
    val status: NotificationStatus,
    val requestedAt: Instant,
    val completedAt: Instant?,
)

class NotificationNotFoundException(
    notificationId: UUID,
) : RuntimeException("Notification $notificationId was not found.")
