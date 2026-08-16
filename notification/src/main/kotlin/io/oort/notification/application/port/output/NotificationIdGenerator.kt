package io.oort.notification.application.port.output

import java.util.UUID

fun interface NotificationIdGenerator {
    fun generate(): UUID
}
