package io.oort.mockserver.notification

import io.kotest.core.spec.style.DescribeSpec
import org.hamcrest.Matchers.blankOrNullString
import org.hamcrest.Matchers.not
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

class MockNotificationControllerTest :
    DescribeSpec({
        val validator =
            LocalValidatorFactoryBean().apply {
                afterPropertiesSet()
            }

        val mockMvc =
            MockMvcBuilders
                .standaloneSetup(MockNotificationController())
                .setValidator(validator)
                .build()

        describe("POST /mock/notifications") {
            it("accepts notification send request") {
                mockMvc
                    .post("/mock/notifications") {
                        contentType = MediaType.APPLICATION_JSON
                        content =
                            """
                            {
                              "channel": "EMAIL",
                              "recipient": "user@example.com",
                              "title": "Order completed",
                              "content": "Your order has been completed."
                            }
                            """.trimIndent()
                    }.andExpect {
                        status { isAccepted() }
                        jsonPath("$.vendorMessageId", not(blankOrNullString()))
                        jsonPath("$.status") { value("ACCEPTED") }
                    }
            }

            it("rejects blank required field") {
                mockMvc
                    .post("/mock/notifications") {
                        contentType = MediaType.APPLICATION_JSON
                        content =
                            """
                            {
                              "channel": "",
                              "recipient": "user@example.com",
                              "title": "Order completed",
                              "content": "Your order has been completed."
                            }
                            """.trimIndent()
                    }.andExpect {
                        status { isBadRequest() }
                    }
            }
        }
    })
