package io.oort.notification.adapter.output.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SpringDataNotificationRepository : JpaRepository<NotificationJpaEntity, UUID>
