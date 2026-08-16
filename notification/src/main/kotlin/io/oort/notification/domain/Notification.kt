package io.oort.notification.domain

import java.time.Instant
import java.util.UUID

class Notification(
    val id: UUID,
    val channel: NotificationChannel,
    val recipient: String,
    val title: String,
    val content: String,
    var status: NotificationStatus,
    val requestedAt: Instant,
    var completedAt: Instant? = null,
) {
    fun markDispatching() {
        check(status == NotificationStatus.ACCEPTED) { "Only accepted notifications can start dispatching." }
        status = NotificationStatus.DISPATCHING
    }

    fun markDispatched(completedAt: Instant) {
        check(status == NotificationStatus.DISPATCHING) { "Only dispatching notifications can be dispatched." }
        status = NotificationStatus.DISPATCHED
        this.completedAt = completedAt
    }

    fun markFailed(completedAt: Instant) {
        check(status == NotificationStatus.DISPATCHING) { "Only dispatching notifications can fail." }
        status = NotificationStatus.FAILED
        this.completedAt = completedAt
    }

    companion object {
        fun accept(
            channel: NotificationChannel,
            recipient: String,
            title: String,
            content: String,
            requestedAt: Instant,
        ): Notification =
            Notification(
                id = UUID.randomUUID(),
                channel = channel,
                recipient = recipient,
                title = title,
                content = content,
                status = NotificationStatus.ACCEPTED,
                requestedAt = requestedAt,
            )
    }
}
