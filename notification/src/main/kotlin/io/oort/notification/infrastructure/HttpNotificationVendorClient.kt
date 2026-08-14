package io.oort.notification.infrastructure

import io.oort.notification.application.CreateNotificationCommand
import io.oort.notification.application.NotificationVendorClient
import io.oort.notification.application.NotificationVendorException
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Component
class HttpNotificationVendorClient(
    restClientBuilder: RestClient.Builder,
    notificationVendorProperties: NotificationVendorProperties,
) : NotificationVendorClient {
    private val restClient = restClientBuilder.baseUrl(notificationVendorProperties.baseUrl).build()

    override fun dispatch(command: CreateNotificationCommand) {
        try {
            restClient
                .post()
                .uri("/mock/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    VendorNotificationRequest(
                        channel = command.channel.name,
                        recipient = command.recipient,
                        title = command.title,
                        content = command.content,
                    ),
                ).retrieve()
                .toBodilessEntity()
        } catch (exception: RestClientException) {
            throw NotificationVendorException(exception)
        }
    }
}

data class VendorNotificationRequest(
    val channel: String,
    val recipient: String,
    val title: String,
    val content: String,
)
