package io.oort.notification.application.port.output

import io.oort.notification.domain.Notification
import java.util.UUID

interface NotificationRepository {
    fun save(notification: Notification): Notification

    fun findById(notificationId: UUID): java.util.Optional<Notification>
}
