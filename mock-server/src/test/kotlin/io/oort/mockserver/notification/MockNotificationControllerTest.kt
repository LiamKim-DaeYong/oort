package io.oort.mockserver.notification

import org.hamcrest.Matchers.blankOrNullString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class MockNotificationControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {
    @Test
    fun `accepts notification send request`() {
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

    @Test
    fun `rejects blank required field`() {
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
