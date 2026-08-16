package io.oort.notification.application.port.input.get

import io.oort.notification.application.port.input.NotificationDetail
import java.util.UUID

interface GetNotificationUseCase {
    fun get(notificationId: UUID): NotificationDetail
}
