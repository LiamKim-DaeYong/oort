package io.oort.notification.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "notifications")
class Notification(
    @field:Id
    val id: UUID,
    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false, length = 20)
    val channel: NotificationChannel,
    @field:Column(nullable = false, length = 320)
    val recipient: String,
    @field:Column(nullable = false, length = 500)
    val title: String,
    @field:Column(nullable = false, length = 10000)
    val content: String,
    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false, length = 20)
    var status: NotificationStatus,
    @field:Column(nullable = false)
    val requestedAt: Instant,
    @field:Column
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

enum class NotificationChannel {
    EMAIL,
    SMS,
    PUSH,
}

enum class NotificationStatus {
    ACCEPTED,
    DISPATCHING,
    DISPATCHED,
    FAILED,
}
