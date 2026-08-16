package io.oort.notification.application.port.input

import java.util.UUID

class NotificationNotFoundException(
    notificationId: UUID,
) : RuntimeException("Notification $notificationId was not found.")
