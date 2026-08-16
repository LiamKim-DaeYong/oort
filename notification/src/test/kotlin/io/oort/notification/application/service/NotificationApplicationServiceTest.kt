package io.oort.notification.application.service

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.oort.notification.application.port.input.create.CreateNotificationCommand
import io.oort.notification.application.port.input.create.CreateNotificationUseCase
import io.oort.notification.application.port.input.get.GetNotificationUseCase
import io.oort.notification.application.port.output.NotificationDispatch
import io.oort.notification.application.port.output.NotificationRepository
import io.oort.notification.application.port.output.NotificationVendorClient
import io.oort.notification.application.port.output.NotificationVendorException
import io.oort.notification.domain.Notification
import io.oort.notification.domain.NotificationChannel
import io.oort.notification.domain.NotificationStatus
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.Optional

class NotificationApplicationServiceTest :
    DescribeSpec({
        val notificationRepository = mockk<NotificationRepository>()
        val notificationVendorClient = mockk<NotificationVendorClient>()
        val requestedAt = Instant.parse("2026-08-14T00:00:00Z")
        val clock = Clock.fixed(requestedAt, ZoneOffset.UTC)
        val service = NotificationApplicationService(notificationRepository, notificationVendorClient, clock)
        val createNotificationUseCase: CreateNotificationUseCase = service
        val getNotificationUseCase: GetNotificationUseCase = service
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
                every {
                    notificationVendorClient.dispatch(
                        NotificationDispatch(
                            channel = command.channel,
                            recipient = command.recipient,
                            title = command.title,
                            content = command.content,
                        ),
                    )
                } returns Unit

                val result = createNotificationUseCase.create(command)

                result.status shouldBe NotificationStatus.DISPATCHED
                result.completedAt shouldBe requestedAt
                verify(exactly = 3) { notificationRepository.save(any()) }
            }

            it("stores a failed result when the vendor call fails") {
                every { notificationVendorClient.dispatch(any()) } throws NotificationVendorException(IllegalStateException())

                val result = createNotificationUseCase.create(command)

                result.status shouldBe NotificationStatus.FAILED
                result.completedAt shouldBe requestedAt
                verify(exactly = 3) { notificationRepository.save(any()) }
            }
        }

        describe("retrieving a notification") {
            it("returns the stored notification detail") {
                val notification =
                    Notification.accept(
                        channel = command.channel,
                        recipient = command.recipient,
                        title = command.title,
                        content = command.content,
                        requestedAt = requestedAt,
                    )
                every { notificationRepository.findById(notification.id) } returns Optional.of(notification)

                val result = getNotificationUseCase.get(notification.id)

                result.id shouldBe notification.id
                result.status shouldBe NotificationStatus.ACCEPTED
            }
        }
    })
