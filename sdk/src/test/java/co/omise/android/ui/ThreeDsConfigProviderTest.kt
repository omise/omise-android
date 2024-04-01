import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.api.Client
import co.omise.android.api.Request
import com.netcetera.threeds.sdk.api.exceptions.InvalidInputException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.spy
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class ThreeDsConfigProviderTest {
    private val client: Client = Mockito.mock()
    private val urlVerifier: AuthorizingPaymentURLVerifier = Mockito.mock()
    private val configProvider = ThreeDSConfigProvider(urlVerifier, client)

    // Inside your test class
    private val configProviderSpy = spy(configProvider)
    private val threeDSConfig: NetceteraConfig =
        NetceteraConfig(
            "Mastercard",
            "algo",
            "encoding",
            """-----BEGIN CERTIFICATE-----\r\nMIIDbzCCAlegAwIBAgIJANp1aztd\r\n-----END CERTIFICATE-----""",
            "TestId",
            "N2OZnlR1fDizs2SO5ukLnmaCupBaHcA=",
            "2.2.0",
        )

    @Before
    fun setUp() {
        whenever(urlVerifier.authorizedURLString).thenReturn("https://example.com/payments/id/authorize?acs=true")
    }

    @Test
    fun createConfigUrlShouldReturnExpectedUrl() =
        runTest {
            val expectedUrl = "https://example.com/payments/id/config"
            assertEquals(expectedUrl, configProvider.createThreeDSConfigUrl(urlVerifier.authorizedURLString))
        }

    @Test
    fun createConfigUrlShouldReturnErrorWhenInvalidUrl() =
        runTest {
            val invalidUrl = "invalid-url"
            val exception =
                assertThrows(InvalidInputException::class.java) {
                    configProvider.createThreeDSConfigUrl(invalidUrl)
                }
            assertEquals("Invalid URL: $invalidUrl", exception.message)
        }

    @Test
    fun getThreeDsConfigUrlShouldReturnThreeDsConfig() =
        runTest {
            whenever(client.send(any<Request<*>>())).thenReturn(threeDSConfig)
            val actualThreeDSConfig = configProviderSpy.getThreeDSConfigs()
            verify(configProviderSpy).createThreeDSConfigUrl(urlVerifier.authorizedURLString)
            val argCaptor = argumentCaptor<Request<*>>()
            verify(client, Mockito.times(1)).send(argCaptor.capture())
            assertEquals(1, argCaptor.allValues.size)
            assertEquals(threeDSConfig, actualThreeDSConfig)
        }
}
