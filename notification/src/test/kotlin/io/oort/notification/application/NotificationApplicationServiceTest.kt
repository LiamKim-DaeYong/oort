package io.oort.notification.application

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.oort.notification.domain.NotificationChannel
import io.oort.notification.domain.NotificationRepository
import io.oort.notification.domain.NotificationStatus
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class NotificationApplicationServiceTest :
    DescribeSpec({
        val notificationRepository = mockk<NotificationRepository>()
        val notificationVendorClient = mockk<NotificationVendorClient>()
        val requestedAt = Instant.parse("2026-08-14T00:00:00Z")
        val clock = Clock.fixed(requestedAt, ZoneOffset.UTC)
        val service = NotificationApplicationService(notificationRepository, notificationVendorClient, clock)
        val command =
            CreateNotificationCommand(
                channel = NotificationChannel.EMAIL,
                recipient = "user@example.com",
                title = "Order completed",
                content = "Your order has been completed.",
            )

        beforeTest {
            clearMocks(notificationRepository, notificationVendorClient)
            every { notificationRepository.save(any()) } answers { firstArg() }
        }

        describe("creating a notification") {
            it("stores the dispatched result after the vendor accepts it") {
                every { notificationVendorClient.dispatch(command) } returns Unit

                val result = service.create(command)

                result.status shouldBe NotificationStatus.DISPATCHED
                result.completedAt shouldBe requestedAt
                verify(exactly = 3) { notificationRepository.save(any()) }
            }

            it("stores a failed result when the vendor call fails") {
                every { notificationVendorClient.dispatch(command) } throws NotificationVendorException(IllegalStateException())

                val result = service.create(command)

                result.status shouldBe NotificationStatus.FAILED
                result.completedAt shouldBe requestedAt
                verify(exactly = 3) { notificationRepository.save(any()) }
            }
        }
    })
