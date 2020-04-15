package pl.droidsonroids.testing.mockwebserver

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

internal class MockResponseBuilderTest {
    private val body = "body"
    private lateinit var builder: MockResponseBuilder
    private lateinit var fixture: Fixture

    @Before
    fun setUp() {
        fixture = Fixture()
        builder = MockResponseBuilder(mock {
            on { parseFrom(any()) } doReturn fixture
        })
    }

    @Test
    fun `body set when present`() {
        fixture.statusCode = 200
        fixture.body = body
        val mockResponse = builder.buildMockResponse("")
        assertThat(mockResponse.status).contains("200")
        assertThat(mockResponse.getBody()!!.readUtf8()).isEqualTo(body)
    }

    @Test
    fun `for each headers added`() {
        fixture.statusCode = 400
        fixture.headers = listOf("name:value", "name2:value2")
        val mockResponse = builder.buildMockResponse("")
        assertThat(mockResponse.status).contains("400")
        assertThat(mockResponse.getBody()).isNull()
        assertThat(mockResponse.headers["name"]).isEqualTo("value")
        assertThat(mockResponse.headers["name2"]).isEqualTo("value2")
    }
}