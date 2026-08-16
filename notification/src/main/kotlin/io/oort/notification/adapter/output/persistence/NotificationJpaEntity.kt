package io.oort.notification.adapter.output.persistence

import io.oort.notification.domain.Notification
import io.oort.notification.domain.NotificationChannel
import io.oort.notification.domain.NotificationStatus
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
class NotificationJpaEntity(
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
    fun toDomain(): Notification =
        Notification(
            id = id,
            channel = channel,
            recipient = recipient,
            title = title,
            content = content,
            status = status,
            requestedAt = requestedAt,
            completedAt = completedAt,
        )

    companion object {
        fun from(notification: Notification): NotificationJpaEntity =
            NotificationJpaEntity(
                id = notification.id,
                channel = notification.channel,
                recipient = notification.recipient,
                title = notification.title,
                content = notification.content,
                status = notification.status,
                requestedAt = notification.requestedAt,
                completedAt = notification.completedAt,
            )
    }
}
