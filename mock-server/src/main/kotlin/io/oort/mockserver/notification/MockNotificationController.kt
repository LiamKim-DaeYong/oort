package io.oort.mockserver.notification

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/mock/notifications")
class MockNotificationController {
    @PostMapping
    fun acceptNotification(
        @Valid @RequestBody request: MockNotificationRequest,
    ): ResponseEntity<MockNotificationAcceptanceResponse> {
        val response =
            MockNotificationAcceptanceResponse(
                vendorMessageId = UUID.randomUUID().toString(),
                status = "ACCEPTED",
            )

        return ResponseEntity.accepted().body(response)
    }
}

data class MockNotificationRequest(
    @field:NotBlank
    val channel: String,
    @field:NotBlank
    val recipient: String,
    @field:NotBlank
    val title: String,
    @field:NotBlank
    val content: String,
)

data class MockNotificationAcceptanceResponse(
    val vendorMessageId: String,
    val status: String,
)
