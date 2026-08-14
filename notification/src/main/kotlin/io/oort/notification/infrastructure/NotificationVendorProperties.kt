package io.oort.notification.infrastructure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("notification.vendor")
data class NotificationVendorProperties(
    val baseUrl: String,
)
