package io.oort.notification.application.port.output

import io.oort.notification.domain.NotificationChannel

interface NotificationVendorClient {
    fun dispatch(notification: NotificationDispatch)
}

data class NotificationDispatch(
    val channel: NotificationChannel,
    val recipient: String,
    val title: String,
    val content: String,
)

class NotificationVendorException(
    cause: Throwable,
) : RuntimeException(cause)
