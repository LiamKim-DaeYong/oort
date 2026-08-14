package io.oort.notification.application

import io.oort.notification.domain.Notification
import io.oort.notification.domain.NotificationChannel
import io.oort.notification.domain.NotificationRepository
import io.oort.notification.domain.NotificationStatus
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant
import java.util.UUID

@Service
class NotificationApplicationService(
    private val notificationRepository: NotificationRepository,
    private val notificationVendorClient: NotificationVendorClient,
    private val clock: Clock,
) {
    fun create(command: CreateNotificationCommand): NotificationResult {
        val notification =
            notificationRepository.save(
                Notification.accept(
                    channel = command.channel,
                    recipient = command.recipient,
                    title = command.title,
                    content = command.content,
                    requestedAt = clock.instant(),
                ),
            )

        notification.markDispatching()
        notificationRepository.save(notification)

        return try {
            notificationVendorClient.dispatch(command)
            notification.markDispatched(clock.instant())
            notificationRepository.save(notification)
            notification.toResult()
        } catch (_: NotificationVendorException) {
            notification.markFailed(clock.instant())
            notificationRepository.save(notification)
            notification.toResult()
        }
    }

    fun get(notificationId: UUID): NotificationResult =
        notificationRepository
            .findById(notificationId)
            .orElseThrow { NotificationNotFoundException(notificationId) }
            .toResult()

    private fun Notification.toResult(): NotificationResult =
        NotificationResult(
            id = id,
            channel = channel,
            recipient = recipient,
            title = title,
            content = content,
            status = status,
            requestedAt = requestedAt,
            completedAt = completedAt,
        )
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

interface NotificationVendorClient {
    fun dispatch(command: CreateNotificationCommand)
}

class NotificationVendorException(
    cause: Throwable,
) : RuntimeException(cause)

class NotificationNotFoundException(
    notificationId: UUID,
) : RuntimeException("Notification $notificationId was not found.")
