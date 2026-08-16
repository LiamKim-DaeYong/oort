package io.oort.notification.adapter.output.persistence

import io.oort.notification.application.port.output.NotificationRepository
import io.oort.notification.domain.Notification
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class NotificationPersistenceAdapter(
    private val springDataNotificationRepository: SpringDataNotificationRepository,
) : NotificationRepository {
    override fun save(notification: Notification): Notification =
        springDataNotificationRepository.save(NotificationJpaEntity.from(notification)).toDomain()

    override fun findById(notificationId: UUID): Optional<Notification> =
        springDataNotificationRepository.findById(notificationId).map(NotificationJpaEntity::toDomain)
}
