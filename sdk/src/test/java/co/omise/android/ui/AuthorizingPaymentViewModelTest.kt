package co.omise.android.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.ThreeDS2ServiceWrapper
import co.omise.android.api.Client
import co.omise.android.api.Request
import co.omise.android.models.Authentication
import co.omise.android.threeds.ThreeDS
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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
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

    private val threeDS: ThreeDS = mock()
    private val client: Client = mock()
    private val urlVerifier: AuthorizingPaymentURLVerifier = mock()
    private val threeDS2Service: ThreeDS2ServiceWrapper = mock()
    private val transaction: Transaction = mock()

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
        whenever(threeDS2Service.createTransaction(any(), any())).thenReturn(transaction)
        threeDS2Service.stub {
            onBlocking { initialize() } doReturn Result.success(Unit)
        }
        whenever(transaction.authenticationRequestParameters).thenReturn(authenticationParams)
    }

    @Test
    fun initialize3DS_shouldInitialize3DS2ServiceAndSendAuthenticationRequest() = runTest {
        AuthorizingPaymentViewModel(threeDS, client, urlVerifier, threeDS2Service, testDispatcher)

        verify(threeDS2Service).initialize()
        verify(client).send(any<Request<Authentication>>())
    }

    @Test
    fun initialize3DS_whenInitialize3DS2ServiceFailedThenSetErrorResult() = runTest {
        threeDS2Service.stub {
            onBlocking { initialize() } doReturn Result.failure(InvalidInputException("Something went wrong."))
        }
        val viewModel = AuthorizingPaymentViewModel(threeDS, client, urlVerifier, threeDS2Service, testDispatcher)

        verify(threeDS2Service).initialize()
        verify(client, never()).send(any<Request<Authentication>>())
        assertEquals(
            "3DS2 initialization failed",
            (viewModel.authenticationResult.value as AuthenticationResult.AuthenticationFailure).error.message
        )
    }

    @Test
    fun sendAuthenticationRequest_whenResponseIsSuccessThenSetSuccessResult() = runTest {
        client.stub {
            onBlocking { send(any<Request<Authentication>>()) } doReturn Authentication(
                status = Authentication.AuthenticationStatus.SUCCESS
            )
        }
        val viewModel = AuthorizingPaymentViewModel(threeDS, client, urlVerifier, threeDS2Service, testDispatcher)

        verify(client).send(any<Request<Authentication>>())
        verify(transaction).close()
        assertEquals(AuthenticationResult.AuthenticationCompleted(TransactionStatus.AUTHENTICATED), viewModel.authenticationResult.value)
    }

    @Test
    fun sendAuthenticationRequest_whenResponseIsChallengeThenSetChallengeResult() = runTest {
        client.stub {
            onBlocking { send(any<Request<Authentication>>()) } doReturn Authentication(
                status = Authentication.AuthenticationStatus.CHALLENGE
            )
        }
        val viewModel = AuthorizingPaymentViewModel(threeDS, client, urlVerifier, threeDS2Service, testDispatcher)

        verify(client).send(any<Request<Authentication>>())
        verify(transaction, never()).close()
        assertEquals(AuthenticationResult.AuthenticationChallenge, viewModel.authenticationResult.value)
    }

    @Test
    fun sendAuthenticationRequest_whenResponseIsChallengeV1ThenSetUnsupportedResult() = runTest {
        client.stub {
            onBlocking { send(any<Request<Authentication>>()) } doReturn Authentication(
                status = Authentication.AuthenticationStatus.CHALLENGE_V1
            )
        }
        val viewModel = AuthorizingPaymentViewModel(threeDS, client, urlVerifier, threeDS2Service, testDispatcher)

        verify(client).send(any<Request<Authentication>>())
        verify(transaction).close()
        assertEquals(AuthenticationResult.AuthenticationUnsupported, viewModel.authenticationResult.value)
    }

    @Test
    fun sendAuthenticationRequest_whenResponseIsFailedThenSetFailureResult() = runTest {
        client.stub {
            onBlocking { send(any<Request<Authentication>>()) } doReturn Authentication(
                status = Authentication.AuthenticationStatus.FAILED
            )
        }
        val viewModel = AuthorizingPaymentViewModel(threeDS, client, urlVerifier, threeDS2Service, testDispatcher)

        verify(client).send(any<Request<Authentication>>())
        verify(transaction).close()
        assertEquals(
            "Authentication failed",
            (viewModel.authenticationResult.value as AuthenticationResult.AuthenticationFailure).error.message
        )
    }

    @Test
    fun doChallenge_shouldExecuteDoChallenge() = runTest {
        client.stub {
            onBlocking { send(any<Request<Authentication>>()) } doReturn Authentication(
                status = Authentication.AuthenticationStatus.CHALLENGE,
                ares = Authentication.ARes(
                    messageVersion = "2.2.0",
                    threeDSServerTransID = UUID.randomUUID().toString(),
                    acsTransID = UUID.randomUUID().toString(),
                    sdkTransID = UUID.randomUUID().toString(),
                    acsSignedContent = "acsSignedContent"
                )
            )
        }
        val viewModel = AuthorizingPaymentViewModel(threeDS, client, urlVerifier, threeDS2Service, testDispatcher)

        viewModel.doChallenge(mock())

        verify(threeDS2Service).doChallenge(any(), any(), any(), eq(5))
    }

    @Test
    fun doChallenge_whenItThrowErrorThenSetFailureResult() = runTest {
        client.stub {
            onBlocking { send(any<Request<Authentication>>()) } doReturn Authentication(
                status = Authentication.AuthenticationStatus.CHALLENGE,
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
        val viewModel = AuthorizingPaymentViewModel(threeDS, client, urlVerifier, threeDS2Service, testDispatcher)

        viewModel.doChallenge(mock())

        assertEquals(
            "Challenge failed",
            (viewModel.authenticationResult.value as AuthenticationResult.AuthenticationFailure).error.message
        )
    }

    @Test
    fun completed_whenReceivedTransactionStatusYThenSetCompletedResult() {
        val completionEvent = CompletionEvent(UUID.randomUUID().toString(), "Y")
        val viewModel = AuthorizingPaymentViewModel(threeDS, client, urlVerifier, threeDS2Service, testDispatcher)

        viewModel.completed(completionEvent)

        assertEquals(AuthenticationResult.AuthenticationCompleted(TransactionStatus.AUTHENTICATED), viewModel.authenticationResult.value)
    }

    @Test
    fun completed_whenReceivedTransactionStatusNThenSetCompletedResult() {
        val viewModel = AuthorizingPaymentViewModel(threeDS, client, urlVerifier, threeDS2Service, testDispatcher)

        viewModel.completed(CompletionEvent(UUID.randomUUID().toString(), "N"))

        assertEquals(
            AuthenticationResult.AuthenticationCompleted(TransactionStatus.NOT_AUTHENTICATED),
            viewModel.authenticationResult.value
        )
    }

    @Test
    fun completed_whenReceivedUnknownTransactionStatusThenSetFailureResult() {
        val viewModel = AuthorizingPaymentViewModel(threeDS, client, urlVerifier, threeDS2Service, testDispatcher)

        viewModel.completed(CompletionEvent(UUID.randomUUID().toString(), "unknown"))

        assertEquals(
            "Challenge completed with unknown status: unknown",
            (viewModel.authenticationResult.value as AuthenticationResult.AuthenticationFailure).error.message
        )
    }

    @Test
    fun cancelled_whenReceivedCancelledEventThenSetFailureResult() {
        val viewModel = AuthorizingPaymentViewModel(threeDS, client, urlVerifier, threeDS2Service, testDispatcher)

        viewModel.cancelled()

        assertEquals(
            "Challenge cancelled",
            (viewModel.authenticationResult.value as AuthenticationResult.AuthenticationFailure).error.message
        )
    }

    @Test
    fun timedout_whenReceivedTimedoutEventThenSetFailureResult() {
        val viewModel = AuthorizingPaymentViewModel(threeDS, client, urlVerifier, threeDS2Service, testDispatcher)

        viewModel.timedout()

        assertEquals(
            "Challenge timedout",
            (viewModel.authenticationResult.value as AuthenticationResult.AuthenticationFailure).error.message
        )
    }

    @Test
    fun protocolError_whenReceivedProtocolErrorEventThenSetFailureResult() {
        val viewModel = AuthorizingPaymentViewModel(threeDS, client, urlVerifier, threeDS2Service, testDispatcher)

        viewModel.protocolError(
            ProtocolErrorEvent(UUID.randomUUID().toString(), ErrorMessage(null, null, null, null, null, null, null))
        )

        assertEquals(
            "Challenge protocol error",
            (viewModel.authenticationResult.value as AuthenticationResult.AuthenticationFailure).error.message
        )
    }

    @Test
    fun runtimeError_whenReceivedRuntimeErrorEventThenSetFailureResult() {
        val viewModel = AuthorizingPaymentViewModel(threeDS, client, urlVerifier, threeDS2Service, testDispatcher)

        viewModel.runtimeError(RuntimeErrorEvent(null, null))

        assertEquals(
            "Challenge runtime error",
            (viewModel.authenticationResult.value as AuthenticationResult.AuthenticationFailure).error.message
        )
    }
}
