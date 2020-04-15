package pl.droidsonroids.testing.mockwebserver

import com.nhaarman.mockitokotlin2.mock
import nl.jqno.equalsverifier.EqualsVerifier
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import pl.droidsonroids.testing.mockwebserver.condition.Condition
import pl.droidsonroids.testing.mockwebserver.condition.PathQueryCondition
import pl.droidsonroids.testing.mockwebserver.condition.PathQueryConditionFactory

private const val INFIX = "/suffix"
private const val PARAMETER_NAME = "param"
private const val PARAMETER_VALUE = "value"

class PathQueryConditionTest {
    private lateinit var suffixPathQueryCondition: PathQueryCondition
    private lateinit var parameterNamePathQueryCondition: PathQueryCondition
    private lateinit var parameterValuePathQueryCondition: PathQueryCondition

    @Before
    fun setUp() {
        val factory = PathQueryConditionFactory("")
        suffixPathQueryCondition = factory.withPathSuffix(INFIX)
        parameterNamePathQueryCondition = factory.withPathSuffixAndQueryParameter(INFIX, PARAMETER_NAME)
        parameterValuePathQueryCondition = factory.withPathSuffixAndQueryParameter(INFIX, PARAMETER_NAME, PARAMETER_VALUE)
    }

    @Test
    fun `has correct suffix`() {
        assertThat(suffixPathQueryCondition.path).isEqualTo(INFIX)
    }

    @Test
    fun `has correct suffix and query parameter name`() {
        assertThat(parameterNamePathQueryCondition.path).isEqualTo(INFIX)
        assertThat(parameterNamePathQueryCondition.queryParameterName).isEqualTo(PARAMETER_NAME)
    }

    @Test
    fun `has correct suffix and query parameter name and value`() {
        assertThat(parameterValuePathQueryCondition.path).isEqualTo(INFIX)
        assertThat(parameterValuePathQueryCondition.queryParameterName).isEqualTo(PARAMETER_NAME)
        assertThat(parameterValuePathQueryCondition.queryParameterValue).isEqualTo(PARAMETER_VALUE)
    }

    @Test
    fun `is name less than suffix`() {
        assertThat(parameterNamePathQueryCondition).isLessThan(suffixPathQueryCondition)
    }

    @Test
    fun `is name greater than value`() {
        assertThat(parameterNamePathQueryCondition).isGreaterThan(parameterValuePathQueryCondition)
    }

    @Test
    fun `is value less than suffix`() {
        assertThat(parameterValuePathQueryCondition).isLessThan(suffixPathQueryCondition)
    }

    @Test
    fun `is unrelated condition not equal to path query condition`() {
        assertThat(parameterValuePathQueryCondition as Condition).isNotEqualByComparingTo(mock())
    }

    @Test
    fun `url with different path does not match`() {
        val url = "http://test.test".toHttpUrlOrNull()!!
        assertThat(suffixPathQueryCondition.isUrlMatching(url)).isFalse()
    }

    @Test
    fun `url with different query does not match`() {
        val url = "http://test.test?another".toHttpUrlOrNull()!!
        assertThat(parameterNamePathQueryCondition.isUrlMatching(url)).isFalse()
    }

    @Test
    fun `url with equal suffix and no query parameter matches`() {
        val url = "http://test.test/suffix".toHttpUrlOrNull()!!
        assertThat(suffixPathQueryCondition.isUrlMatching(url)).isTrue()
    }

    @Test
    fun `url with suffix and query parameter name matches`() {
        val url = "http://test.test/suffix?param".toHttpUrlOrNull()!!
        assertThat(parameterNamePathQueryCondition.isUrlMatching(url)).isTrue()
    }

    @Test
    fun `url with equal suffix and different query parameter name does not match`() {
        val url = "http://test.test/suffix?param2".toHttpUrlOrNull()!!
        assertThat(parameterNamePathQueryCondition.isUrlMatching(url)).isFalse()
    }

    @Test
    fun `url with suffix, query parameter name and value matches`() {
        val url = "http://test.test/suffix?param=value".toHttpUrlOrNull()!!
        assertThat(parameterValuePathQueryCondition.isUrlMatching(url)).isTrue()
    }

    @Test
    fun `url with equal suffix, query parameter name with different value does not match`() {
        val url = "http://test.test/suffix?param=value2".toHttpUrlOrNull()!!
        assertThat(parameterValuePathQueryCondition.isUrlMatching(url)).isFalse()
    }

    @Test
    fun `equals and hashCode match contract`() {
        EqualsVerifier.forClass(PathQueryCondition::class.java).verify()
    }
}