package io.oort.notification.adapter.output.vendor

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("notification.vendor")
data class NotificationVendorProperties(
    val baseUrl: String,
)
