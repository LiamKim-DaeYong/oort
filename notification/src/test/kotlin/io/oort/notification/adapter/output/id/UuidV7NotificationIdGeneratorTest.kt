package io.oort.notification.adapter.output.id

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class UuidV7NotificationIdGeneratorTest :
    DescribeSpec({
        describe("UUID v7 notification ID generator") {
            it("generates unique version 7 UUIDs") {
                val notificationIdGenerator = UuidV7NotificationIdGenerator()
                val ids = List(1_000) { notificationIdGenerator.generate() }

                ids.toSet() shouldHaveSize ids.size
                ids.all { it.version() == 7 } shouldBe true
            }
        }
    })
