package io.oort.notification.adapter.output.vendor

import io.oort.notification.application.port.output.NotificationDispatch
import io.oort.notification.application.port.output.NotificationVendorClient
import io.oort.notification.application.port.output.NotificationVendorException
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

    override fun dispatch(notification: NotificationDispatch) {
        try {
            restClient
                .post()
                .uri("/mock/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    VendorNotificationRequest(
                        channel = notification.channel.name,
                        recipient = notification.recipient,
                        title = notification.title,
                        content = notification.content,
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
