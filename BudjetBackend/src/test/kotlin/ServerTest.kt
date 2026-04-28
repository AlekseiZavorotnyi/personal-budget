package com

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.*

class ServerTest {

    @Test
    fun `test root endpoint`() = testApplication {
        application {
            module(testing = true)
        }

        assertEquals(HttpStatusCode.OK, client.get("/").status)
    }

    @Test
    fun `test api health endpoint`() = testApplication {
        application {
            module(testing = true)
        }

        val response = client.get("/api/health")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("\"status\":\"ok\""))
    }

    @Test
    fun `test transactions endpoint`() = testApplication {
        application {
            module(testing = true)
        }

        val response = client.get("/api/transactions")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

}
