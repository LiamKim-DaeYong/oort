package io.oort.mockserver

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class MockServerApplicationTest :
    DescribeSpec({
        describe("mock-server skeleton") {
            it("loads the test runtime") {
                true shouldBe true
            }
        }
    })
