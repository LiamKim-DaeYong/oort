package io.oort.notification.application.port.input.create

import io.oort.notification.application.port.input.NotificationDetail

interface CreateNotificationUseCase {
    fun create(command: CreateNotificationCommand): NotificationDetail
}
