package co.omise.android.ui

import NetceteraConfig
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.OmiseException
import co.omise.android.ThreeDS2ServiceWrapper
import co.omise.android.api.Client
import co.omise.android.api.Request
import co.omise.android.models.Authentication
import co.omise.android.models.Authentication.AuthenticationStatus
import co.omise.android.models.AuthenticationAPIError
import com.netcetera.threeds.sdk.api.exceptions.InvalidInputException
import com.netcetera.threeds.sdk.api.exceptions.SDKRuntimeException
import com.netcetera.threeds.sdk.api.transaction.AuthenticationRequestParameters
import com.netcetera.threeds.sdk.api.transaction.Transaction
import com.netcetera.threeds.sdk.api.transaction.challenge.ErrorMessage
import com.netcetera.threeds.sdk.api.transaction.challenge.events.CompletionEvent
import com.netcetera.threeds.sdk.api.transaction.challenge.events.ProtocolErrorEvent
import com.netcetera.threeds.sdk.api.transaction.challenge.events.RuntimeErrorEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AuthorizingPaymentViewModelTest {
    @get:Rule
    val instanceExecutor = InstantTaskExecutorRule()

    private val client: Client = mock()
    private val urlVerifier: AuthorizingPaymentURLVerifier = mock()
    private val threeDS2Service: ThreeDS2ServiceWrapper = mock()
    private val transaction: Transaction = mock()
    private val netceteraConfig =
        NetceteraConfig(
            "Mastercard",
            "algo",
            "encoding",
            """-----BEGIN CERTIFICATE-----\r\nMIIDbzCCAlegAwIBAgIJANp1aztd\r\n-----END CERTIFICATE-----""",
            "TestId",
            "N2OZnlR1fDizs2SO5ukLnmaCupBaHcA=",
            "2.2.0",
        )
    private val threeDSRequestorAppURL = "sampleapp://omise.co/authorize_return"

    private val testDispatcher = UnconfinedTestDispatcher()

    private val authenticationParams =
        AuthenticationRequestParameters(
            UUID.randomUUID().toString(),
            "sdkTrans_1234",
            "{\"kty\":\"EC\",\"x\":\"xxx\",\"y\":\"xxx\",\"crv\":\"P-256\"}",
            "co.omise.app",
            "ref_1234",
            "2.2.0",
        )

    @Before
    fun setUp() {
        whenever(urlVerifier.authorizedURLString).thenReturn("https://www.omise.co/pay")
        whenever(urlVerifier.verifyExternalURL()).thenReturn(false)
        whenever(threeDS2Service.transaction).thenReturn(transaction)
        whenever(threeDS2Service.createTransaction(any(), any())).thenReturn(transaction)
        threeDS2Service.stub {
            onBlocking { initialize(netceteraConfig) } doReturn Result.success(Unit)
        }
        whenever(transaction.authenticationRequestParameters).thenReturn(authenticationParams)
    }

    @Test
    fun createConfigUrlShouldReturnExpectedUrl() =
        runTest {
            val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL, testDispatcher)
            val createdConfigUrl = viewModel.createNetceteraConfigUrl("https://example.com/payments/id/authorize")
            val expectedUrl = "https://example.com/payments/id/config"
            assertEquals(expectedUrl, createdConfigUrl)
        }

    @Test
    fun createConfigUrlShouldReturnErrorWhenInvalidUrl() =
        runTest {
            val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL, testDispatcher)
            val invalidUrl = "invalid-url"
            val exception =
                assertThrows(InvalidInputException::class.java) {
                    viewModel.createNetceteraConfigUrl(invalidUrl)
                }
            assertEquals("Invalid URL: $invalidUrl", exception.message)
        }

    @Test
    fun initialize3DS_shouldInitialize3DS2ServiceAndSendAuthenticationRequest() =
        runTest {
            AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL, testDispatcher)
            threeDS2Service.initialize(netceteraConfig)
            verify(threeDS2Service).initialize(netceteraConfig)
            verify(client).send(any<Request<NetceteraConfig>>())
            verify(client).send(any<Request<Authentication>>())
        }

    @Test
    fun initialize3DS_whenInitialize3DS2ServiceFailedThenSetError() =
        runTest {
            whenever(client.send(any<Request<*>>())).thenReturn(netceteraConfig)
            threeDS2Service.stub {
                onBlocking { initialize(netceteraConfig) } doReturn Result.failure(InvalidInputException("Something went wrong."))
            }
            val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL, testDispatcher)

            verify(threeDS2Service).initialize(netceteraConfig)
            val argCaptor = argumentCaptor<Request<*>>()
            verify(client, times(1)).send(argCaptor.capture())
            assertEquals(1, argCaptor.allValues.size)
            assertEquals(OmiseSDKError.THREE_DS2_INITIALIZATION_FAILED.value, (viewModel.error.value as OmiseException).message)
        }

    @Test
    fun sendAuthenticationRequest_whenResponseIsSuccessThenSetSuccessStatus() =
        runTest {
            whenever(client.send(any<Request<*>>())).thenReturn(netceteraConfig).thenReturn(Authentication(AuthenticationStatus.SUCCESS))

            val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL, testDispatcher)
            val argCaptor = argumentCaptor<Request<*>>()
            verify(client, times(2)).send(argCaptor.capture())
            assertEquals(2, argCaptor.allValues.size)
            verify(transaction).close()
            assertEquals(AuthenticationStatus.SUCCESS, viewModel.authenticationStatus.value)
        }

    @Test
    fun sendAuthenticationRequest_whenResponseIsChallengeThenSetChallengeStatus() =
        runTest {
            whenever(client.send(any<Request<*>>())).thenReturn(netceteraConfig).thenReturn(Authentication(AuthenticationStatus.CHALLENGE))
            val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL, testDispatcher)

            val argCaptor = argumentCaptor<Request<*>>()
            verify(client, times(2)).send(argCaptor.capture())
            assertEquals(2, argCaptor.allValues.size)
            verify(transaction, never()).close()
            assertTrue(viewModel.isLoading.value!!)
            assertEquals(AuthenticationStatus.CHALLENGE, viewModel.authenticationStatus.value)
        }

    @Test
    fun sendAuthenticationRequest_whenResponseIsChallengeV1ThenSetChallengeV1Status() =
        runTest {
            whenever(
                client.send(any<Request<*>>()),
            ).thenReturn(netceteraConfig).thenReturn(Authentication(AuthenticationStatus.CHALLENGE_V1))
            val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL, testDispatcher)

            val argCaptor = argumentCaptor<Request<*>>()
            verify(client, times(2)).send(argCaptor.capture())
            assertEquals(2, argCaptor.allValues.size)
            verify(transaction).close()
            assertEquals(AuthenticationStatus.CHALLENGE_V1, viewModel.authenticationStatus.value)
        }

    @Test
    fun sendAuthenticationRequest_whenResponseIsFailedThenSetFailedStatus() =
        runTest {
            whenever(client.send(any<Request<*>>())).thenReturn(netceteraConfig).thenReturn(Authentication(AuthenticationStatus.FAILED))
            val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL, testDispatcher)

            val argCaptor = argumentCaptor<Request<*>>()
            verify(client, times(2)).send(argCaptor.capture())
            assertEquals(2, argCaptor.allValues.size)
            verify(transaction).close()
            assertEquals(AuthenticationStatus.FAILED, viewModel.authenticationStatus.value)
        }

    @Test
    fun sendAuthenticationRequest_whenThrowErrorThenSetFailedStatus() =
        runTest {
            client.stub {
                onBlocking { send(any<Request<Authentication>>()) } doThrow
                    AuthenticationAPIError(
                        status = AuthenticationStatus.FAILED,
                        message = "Something went wrong.",
                    )
            }
            val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL, testDispatcher)

            verify(client).send(any<Request<Authentication>>())
            verify(transaction).close()
            assertEquals(AuthenticationStatus.FAILED.message, viewModel.error.value?.message)
        }

    @Test
    fun createThreeDSRequestorAppURL_should_add_transactionId_query_param() =
        runTest {
            val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL, testDispatcher)
            val sdkTransactionId = "123"
            val updatedUrl = viewModel.createThreeDSRequestorAppURL(sdkTransactionId)
            val expectedUrl = "$threeDSRequestorAppURL?transID=$sdkTransactionId"
            assertEquals(updatedUrl, expectedUrl)
        }

    @Test
    fun createThreeDSRequestorAppURL_should_not_ignore_already_existing_query_params() =
        runTest {
            val urlWithParams = "$threeDSRequestorAppURL?param=value"
            val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, urlWithParams, testDispatcher)
            val sdkTransactionId = "123"
            val updatedUrl = viewModel.createThreeDSRequestorAppURL(sdkTransactionId)
            val expectedUrl = "$urlWithParams&transID=$sdkTransactionId"
            assertEquals(updatedUrl, expectedUrl)
        }

    @Test
    fun doChallenge_shouldExecuteDoChallenge() =
        runTest {
            whenever(client.send(any<Request<*>>())).thenReturn(netceteraConfig).thenReturn(
                Authentication(
                    status = AuthenticationStatus.CHALLENGE,
                    ares =
                        Authentication.ARes(
                            messageVersion = "2.2.0",
                            threeDSServerTransID = UUID.randomUUID().toString(),
                            acsTransID = UUID.randomUUID().toString(),
                            sdkTransID = UUID.randomUUID().toString(),
                            acsSignedContent = "acsSignedContent",
                            acsReferenceNumber = "3DS_LOA_ACS",
                        ),
                ),
            )
            val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL, testDispatcher)

            viewModel.doChallenge(mock())

            verify(threeDS2Service).doChallenge(any(), any(), any(), eq(5))
        }

    @Test
    fun doChallenge_whenItThrowErrorThenSetError() =
        runTest {
            whenever(client.send(any<Request<*>>())).thenReturn(netceteraConfig).thenReturn(
                Authentication(
                    status = AuthenticationStatus.CHALLENGE,
                    ares =
                        Authentication.ARes(
                            messageVersion = "2.2.0",
                            threeDSServerTransID = UUID.randomUUID().toString(),
                            acsTransID = UUID.randomUUID().toString(),
                            sdkTransID = UUID.randomUUID().toString(),
                            acsSignedContent = "acsSignedContent",
                            acsReferenceNumber = "3DS_LOA_ACS",
                        ),
                ),
            )
            whenever(threeDS2Service.doChallenge(any(), any(), any(), any())).doThrow(SDKRuntimeException("Something went wrong.", null))
            val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL, testDispatcher)

            viewModel.doChallenge(mock())

            assertEquals(ChallengeStatus.FAILED.value, (viewModel.error.value as OmiseException).message)
        }

    @Test
    fun completed_whenReceivedTransactionStatusYThenSetAuthenticatedStatus() {
        val completionEvent = CompletionEvent(UUID.randomUUID().toString(), "Y")
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL, testDispatcher)

        viewModel.completed(completionEvent)

        assertEquals(TransactionStatus.AUTHENTICATED, viewModel.transactionStatus.value)
    }

    @Test
    fun completed_whenReceivedTransactionStatusNThenSetNotAuthenticatedStatus() {
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL, testDispatcher)

        viewModel.completed(CompletionEvent(UUID.randomUUID().toString(), "N"))

        assertEquals(TransactionStatus.NOT_AUTHENTICATED, viewModel.transactionStatus.value)
    }

    @Test
    fun completed_whenReceivedUnknownTransactionStatusThenSetError() {
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL, testDispatcher)
        val unknownStatus = "unknown"
        viewModel.completed(CompletionEvent(UUID.randomUUID().toString(), unknownStatus))

        assertEquals(
            ChallengeStatus.COMPLETED_WITH_UNKNOWN_STATUS.includeUnknownTransactionStatusWithError(unknownStatus),
            (viewModel.error.value as OmiseException).message,
        )
    }

    @Test
    fun cancelled_whenReceivedCancelledEventThenSetError() {
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL, testDispatcher)

        viewModel.cancelled()

        assertEquals(ChallengeStatus.CANCELLED.value, (viewModel.error.value as OmiseException).message)
    }

    @Test
    fun timedout_whenReceivedTimedoutEventThenSetError() {
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL, testDispatcher)

        viewModel.timedout()

        assertEquals(ChallengeStatus.TIMED_OUT.value, (viewModel.error.value as OmiseException).message)
    }

    @Test
    fun protocolError_whenReceivedProtocolErrorEventThenSetError() {
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL, testDispatcher)

        viewModel.protocolError(
            ProtocolErrorEvent(
                UUID.randomUUID().toString(),
                ErrorMessage(
                    // transactionID
                    UUID.randomUUID().toString(),
                    // errorCode
                    "203",
                    // errorDescription
                    "Invalid data",
                    // errorDetail
                    "dsURL",
                    // errorComponent
                    "A",
                    // errorMessageType
                    "CReq",
                    // messageVersionNumber
                    "2.2.0",
                ),
            ),
        )

        assertEquals(ChallengeStatus.PROTOCOL_ERROR.value, (viewModel.error.value as OmiseException).message)
    }

    @Test
    fun runtimeError_whenReceivedRuntimeErrorEventThenSetError() {
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL, testDispatcher)

        viewModel.runtimeError(
            RuntimeErrorEvent(
                // errorCode
                "402",
                // errorMessage
                "Challenge runtime error",
            ),
        )

        assertEquals(ChallengeStatus.RUNTIME_ERROR.value, (viewModel.error.value as OmiseException).message)
    }
}
