package io.oort.notification.adapter.input.web

import io.oort.notification.application.port.input.CreateNotificationCommand
import io.oort.notification.application.port.input.NotificationResult
import io.oort.notification.application.port.input.NotificationUseCase
import io.oort.notification.domain.NotificationChannel
import io.oort.notification.domain.NotificationStatus
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.time.Instant
import java.util.UUID

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController(
    private val notificationUseCase: NotificationUseCase,
) {
    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateNotificationRequest,
    ): ResponseEntity<NotificationResponse> {
        val notification = notificationUseCase.create(request.toCommand())
        val location =
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{notificationId}")
                .buildAndExpand(notification.id)
                .toUri()

        return ResponseEntity.created(location).body(notification.toResponse())
    }

    @GetMapping("/{notificationId}")
    fun get(
        @PathVariable notificationId: UUID,
    ): NotificationResponse = notificationUseCase.get(notificationId).toResponse()
}

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

data class NotificationResponse(
    val id: UUID,
    val channel: NotificationChannel,
    val recipient: String,
    val title: String,
    val content: String,
    val status: NotificationStatus,
    val requestedAt: Instant,
    val completedAt: Instant?,
)

private fun NotificationResult.toResponse(): NotificationResponse =
    NotificationResponse(
        id = id,
        channel = channel,
        recipient = recipient,
        title = title,
        content = content,
        status = status,
        requestedAt = requestedAt,
        completedAt = completedAt,
    )
