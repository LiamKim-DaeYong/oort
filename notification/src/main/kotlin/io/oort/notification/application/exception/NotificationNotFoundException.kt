package io.oort.notification.application.exception

import java.util.UUID

class NotificationNotFoundException(
    notificationId: UUID,
) : RuntimeException("Notification $notificationId was not found.")
