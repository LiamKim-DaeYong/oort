package io.oort.notification.domain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.Instant

class NotificationTest :
    DescribeSpec({
        val requestedAt = Instant.parse("2026-08-14T00:00:00Z")

        describe("notification status") {
            it("transitions from accepted to dispatched") {
                val notification = notification(requestedAt)

                notification.markDispatching()
                notification.markDispatched(requestedAt.plusSeconds(1))

                notification.status shouldBe NotificationStatus.DISPATCHED
                notification.completedAt shouldBe requestedAt.plusSeconds(1)
            }

            it("transitions from dispatching to failed") {
                val notification = notification(requestedAt)

                notification.markDispatching()
                notification.markFailed(requestedAt.plusSeconds(1))

                notification.status shouldBe NotificationStatus.FAILED
                notification.completedAt shouldNotBe null
            }
        }
    }) {
    companion object {
        private fun notification(requestedAt: Instant): Notification =
            Notification.accept(
                channel = NotificationChannel.EMAIL,
                recipient = "user@example.com",
                title = "Order completed",
                content = "Your order has been completed.",
                requestedAt = requestedAt,
            )
    }
}
