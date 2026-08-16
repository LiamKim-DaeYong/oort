package io.oort.notification.config

import io.oort.notification.adapter.output.vendor.NotificationVendorProperties
import io.oort.notification.application.port.output.NotificationIdGenerator
import io.oort.notification.application.port.output.NotificationRepository
import io.oort.notification.application.port.output.NotificationVendorClient
import io.oort.notification.application.service.NotificationApplicationService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import java.time.Clock

@Configuration
@EnableConfigurationProperties(NotificationVendorProperties::class)
class NotificationConfiguration {
    @Bean
    fun clock(): Clock = Clock.systemUTC()

    @Bean
    fun restClientBuilder(): RestClient.Builder = RestClient.builder()

    @Bean
    fun notificationApplicationService(
        notificationRepository: NotificationRepository,
        notificationVendorClient: NotificationVendorClient,
        notificationIdGenerator: NotificationIdGenerator,
        clock: Clock,
    ): NotificationApplicationService =
        NotificationApplicationService(notificationRepository, notificationVendorClient, notificationIdGenerator, clock)
}
