package io.oort.notification.application.service

import io.oort.notification.application.port.input.CreateNotificationCommand
import io.oort.notification.application.port.input.NotificationNotFoundException
import io.oort.notification.application.port.input.NotificationResult
import io.oort.notification.application.port.input.NotificationUseCase
import io.oort.notification.application.port.output.NotificationDispatch
import io.oort.notification.application.port.output.NotificationRepository
import io.oort.notification.application.port.output.NotificationVendorClient
import io.oort.notification.application.port.output.NotificationVendorException
import io.oort.notification.domain.Notification
import java.time.Clock
import java.util.UUID

class NotificationApplicationService(
    private val notificationRepository: NotificationRepository,
    private val notificationVendorClient: NotificationVendorClient,
    private val clock: Clock,
) : NotificationUseCase {
    override fun create(command: CreateNotificationCommand): NotificationResult {
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
            notificationVendorClient.dispatch(command.toDispatch())
            notification.markDispatched(clock.instant())
            notificationRepository.save(notification)
            notification.toResult()
        } catch (_: NotificationVendorException) {
            notification.markFailed(clock.instant())
            notificationRepository.save(notification)
            notification.toResult()
        }
    }

    override fun get(notificationId: UUID): NotificationResult =
        notificationRepository
            .findById(notificationId)
            .orElseThrow { NotificationNotFoundException(notificationId) }
            .toResult()

    private fun CreateNotificationCommand.toDispatch(): NotificationDispatch =
        NotificationDispatch(
            channel = channel,
            recipient = recipient,
            title = title,
            content = content,
        )

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
