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
    fun sendNotification(
        @Valid @RequestBody request: MockNotificationSendRequest,
    ): ResponseEntity<MockNotificationSendResponse> {
        val response =
            MockNotificationSendResponse(
                vendorMessageId = UUID.randomUUID().toString(),
                status = "ACCEPTED",
            )

        return ResponseEntity.accepted().body(response)
    }
}

data class MockNotificationSendRequest(
    @field:NotBlank
    val channel: String,
    @field:NotBlank
    val recipient: String,
    @field:NotBlank
    val title: String,
    @field:NotBlank
    val content: String,
)

data class MockNotificationSendResponse(
    val vendorMessageId: String,
    val status: String,
)
