package io.oort.notification.config

import io.oort.notification.infrastructure.NotificationVendorProperties
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
}
