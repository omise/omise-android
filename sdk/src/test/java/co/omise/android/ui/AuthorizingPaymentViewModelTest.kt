package co.omise.android.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID


@ExperimentalCoroutinesApi
class AuthorizingPaymentViewModelTest {
    @get:Rule
    val instanceExecutor = InstantTaskExecutorRule()

    private val client: Client = mock()
    private val urlVerifier: AuthorizingPaymentURLVerifier = mock()
    private val threeDS2Service: ThreeDS2ServiceWrapper = mock()
    private val transaction: Transaction = mock()
    private val threeDSRequestorAppURL = "sampleapp://omise.co/authorize_return"

    private val testDispatcher = UnconfinedTestDispatcher()

    private val authenticationParams = AuthenticationRequestParameters(
        UUID.randomUUID().toString(),
        "sdkTrans_1234",
        "{\"kty\":\"EC\",\"x\":\"xxx\",\"y\":\"xxx\",\"crv\":\"P-256\"}",
        "co.omise.app",
        "ref_1234",
        "2.2.0"
    )

    @Before
    fun setUp() {
        whenever(urlVerifier.authorizedURLString).thenReturn("https://www.omise.co/pay")
        whenever(urlVerifier.verifyExternalURL()).thenReturn(false)
        whenever(threeDS2Service.transaction).thenReturn(transaction)
        whenever(threeDS2Service.createTransaction(any(), any())).thenReturn(transaction)
        threeDS2Service.stub {
            onBlocking { initialize() } doReturn Result.success(Unit)
        }
        whenever(transaction.authenticationRequestParameters).thenReturn(authenticationParams)
    }

    @Test
    fun initialize3DS_shouldInitialize3DS2ServiceAndSendAuthenticationRequest() = runTest {
        AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL,testDispatcher)

        verify(threeDS2Service).initialize()
        verify(client).send(any<Request<Authentication>>())
    }

    @Test
    fun initialize3DS_whenInitialize3DS2ServiceFailedThenSetError() = runTest {
        threeDS2Service.stub {
            onBlocking { initialize() } doReturn Result.failure(InvalidInputException("Something went wrong."))
        }
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL,testDispatcher)

        verify(threeDS2Service).initialize()
        verify(client, never()).send(any<Request<Authentication>>())
        assertEquals("3DS2 initialization failed", (viewModel.error.value as OmiseException).message)
    }

    @Test
    fun sendAuthenticationRequest_whenResponseIsSuccessThenSetSuccessStatus() = runTest {
        client.stub {
            onBlocking { send(any<Request<Authentication>>()) } doReturn Authentication(
                status = AuthenticationStatus.SUCCESS
            )
        }
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL,testDispatcher)

        verify(client).send(any<Request<Authentication>>())
        verify(transaction).close()
        assertEquals(AuthenticationStatus.SUCCESS, viewModel.authenticationStatus.value)
    }

    @Test
    fun sendAuthenticationRequest_whenResponseIsChallengeThenSetChallengeStatus() = runTest {
        client.stub {
            onBlocking { send(any<Request<Authentication>>()) } doReturn Authentication(
                status = AuthenticationStatus.CHALLENGE
            )
        }
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL,testDispatcher)

        verify(client).send(any<Request<Authentication>>())
        verify(transaction, never()).close()
        assertTrue(viewModel.isLoading.value!!)
        assertEquals(AuthenticationStatus.CHALLENGE, viewModel.authenticationStatus.value)
    }

    @Test
    fun sendAuthenticationRequest_whenResponseIsChallengeV1ThenSetChallengeV1Status() = runTest {
        client.stub {
            onBlocking { send(any<Request<Authentication>>()) } doReturn Authentication(
                status = AuthenticationStatus.CHALLENGE_V1
            )
        }
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL,testDispatcher)

        verify(client).send(any<Request<Authentication>>())
        verify(transaction).close()
        assertEquals(AuthenticationStatus.CHALLENGE_V1, viewModel.authenticationStatus.value)
    }

    @Test
    fun sendAuthenticationRequest_whenResponseIsFailedThenSetFailedStatus() = runTest {
        client.stub {
            onBlocking { send(any<Request<Authentication>>()) } doReturn Authentication(
                status = AuthenticationStatus.FAILED
            )
        }
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL,testDispatcher)

        verify(client).send(any<Request<Authentication>>())
        verify(transaction).close()
        assertEquals(AuthenticationStatus.FAILED, viewModel.authenticationStatus.value)
    }

    @Test
    fun sendAuthenticationRequest_whenThrowErrorThenSetFailedStatus() = runTest {
        client.stub {
            onBlocking { send(any<Request<Authentication>>()) } doThrow AuthenticationAPIError(
                status = AuthenticationStatus.FAILED,
                message = "Something went wrong."
            )
        }
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL,testDispatcher)

        verify(client).send(any<Request<Authentication>>())
        verify(transaction).close()
        assertEquals("Authentication failed.", viewModel.error.value?.message)
    }

    @Test
    fun doChallenge_shouldExecuteDoChallenge() = runTest {
        client.stub {
            onBlocking { send(any<Request<Authentication>>()) } doReturn Authentication(
                status = AuthenticationStatus.CHALLENGE,
                ares = Authentication.ARes(
                    messageVersion = "2.2.0",
                    threeDSServerTransID = UUID.randomUUID().toString(),
                    acsTransID = UUID.randomUUID().toString(),
                    sdkTransID = UUID.randomUUID().toString(),
                    acsSignedContent = "acsSignedContent"
                )
            )
        }
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL,testDispatcher)

        viewModel.doChallenge(mock())

        verify(threeDS2Service).doChallenge(any(), any(), any(), eq(5))
    }

    @Test
    fun doChallenge_whenItThrowErrorThenSetError() = runTest {
        client.stub {
            onBlocking { send(any<Request<Authentication>>()) } doReturn Authentication(
                status = AuthenticationStatus.CHALLENGE,
                ares = Authentication.ARes(
                    messageVersion = "2.2.0",
                    threeDSServerTransID = UUID.randomUUID().toString(),
                    acsTransID = UUID.randomUUID().toString(),
                    sdkTransID = UUID.randomUUID().toString(),
                    acsSignedContent = "acsSignedContent"
                )
            )
        }
        whenever(threeDS2Service.doChallenge(any(), any(), any(), any())).doThrow(SDKRuntimeException("Something went wrong.", null))
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL,testDispatcher)

        viewModel.doChallenge(mock())

        assertEquals("Challenge failed", (viewModel.error.value as OmiseException).message)
    }

    @Test
    fun completed_whenReceivedTransactionStatusYThenSetAuthenticatedStatus() {
        val completionEvent = CompletionEvent(UUID.randomUUID().toString(), "Y")
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL,testDispatcher)

        viewModel.completed(completionEvent)

        assertEquals(TransactionStatus.AUTHENTICATED, viewModel.transactionStatus.value)
    }

    @Test
    fun completed_whenReceivedTransactionStatusNThenSetNotAuthenticatedStatus() {
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL,testDispatcher)

        viewModel.completed(CompletionEvent(UUID.randomUUID().toString(), "N"))

        assertEquals(TransactionStatus.NOT_AUTHENTICATED, viewModel.transactionStatus.value)
    }

    @Test
    fun completed_whenReceivedUnknownTransactionStatusThenSetError() {
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL,testDispatcher)

        viewModel.completed(CompletionEvent(UUID.randomUUID().toString(), "unknown"))

        assertEquals("Challenge completed with unknown status: unknown", (viewModel.error.value as OmiseException).message)
    }

    @Test
    fun cancelled_whenReceivedCancelledEventThenSetError() {
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL,testDispatcher)

        viewModel.cancelled()

        assertEquals("Challenge cancelled", (viewModel.error.value as OmiseException).message)
    }

    @Test
    fun timedout_whenReceivedTimedoutEventThenSetError() {
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL,testDispatcher)

        viewModel.timedout()

        assertEquals("Challenge timedout", (viewModel.error.value as OmiseException).message)
    }

    @Test
    fun protocolError_whenReceivedProtocolErrorEventThenSetError() {
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL,testDispatcher)

        viewModel.protocolError(
            ProtocolErrorEvent(
                UUID.randomUUID().toString(), ErrorMessage(
                    UUID.randomUUID().toString(),   // transactionID
                    "203",                          // errorCode
                    "Invalid data",                 // errorDescription
                    "dsURL",                        // errorDetail
                    "A",                            // errorComponent
                    "CReq",                         // errorMessageType
                    "2.2.0"                         // messageVersionNumber
                )
            )
        )

        assertEquals("Challenge protocol error", (viewModel.error.value as OmiseException).message)
    }

    @Test
    fun runtimeError_whenReceivedRuntimeErrorEventThenSetError() {
        val viewModel = AuthorizingPaymentViewModel(client, urlVerifier, threeDS2Service, threeDSRequestorAppURL,testDispatcher)

        viewModel.runtimeError(
            RuntimeErrorEvent(
                "402",                      // errorCode
                "Challenge runtime error"   // errorMessage
            )
        )

        assertEquals("Challenge runtime error", (viewModel.error.value as OmiseException).message)
    }
}
