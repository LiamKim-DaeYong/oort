package io.oort.notification

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class NotificationApplicationTest :
    DescribeSpec({
        describe("notification skeleton") {
            it("loads the test runtime") {
                true shouldBe true
            }
        }
    })
