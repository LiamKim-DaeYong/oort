package io.oort.notification.adapter.input.web.dto

import io.oort.notification.application.port.input.create.CreateNotificationCommand
import io.oort.notification.domain.NotificationChannel
import jakarta.validation.constraints.NotBlank

data class CreateNotificationRequest(
    val channel: NotificationChannel,
    @field:NotBlank
    val recipient: String,
    @field:NotBlank
    val title: String,
    @field:NotBlank
    val content: String,
) {
    fun toCommand(): CreateNotificationCommand =
        CreateNotificationCommand(
            channel = channel,
            recipient = recipient,
            title = title,
            content = content,
        )
}
