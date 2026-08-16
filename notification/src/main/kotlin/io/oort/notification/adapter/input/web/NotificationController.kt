package io.oort.notification.adapter.input.web

import io.oort.notification.adapter.input.web.dto.CreateNotificationRequest
import io.oort.notification.adapter.input.web.dto.NotificationResponse
import io.oort.notification.adapter.input.web.dto.toResponse
import io.oort.notification.application.port.input.create.CreateNotificationUseCase
import io.oort.notification.application.port.input.get.GetNotificationUseCase
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.UUID

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController(
    private val createNotificationUseCase: CreateNotificationUseCase,
    private val getNotificationUseCase: GetNotificationUseCase,
) {
    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateNotificationRequest,
    ): ResponseEntity<NotificationResponse> {
        val notification = createNotificationUseCase.create(request.toCommand())
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
    ): NotificationResponse = getNotificationUseCase.get(notificationId).toResponse()
}
