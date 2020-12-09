package co.omise.android.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import co.omise.android.api.Client
import co.omise.android.api.Request
import co.omise.android.models.APIError
import co.omise.android.models.ChargeStatus
import co.omise.android.models.Token
import co.omise.android.threeds.ThreeDS
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class AuthorizingPaymentViewModelTest {

    private val client: Client = mock()
    private val threeDS: ThreeDS = mock()
    private val tokenID = "tokn_test_1234"
    private val authorizeUrl = "https://www.omise.co/pay"
    private val dispatcher = TestCoroutineDispatcher()
    private val coroutineScope = TestCoroutineScope(dispatcher)
    private val viewModel = AuthorizingPaymentViewModel(client, threeDS, tokenID, coroutineScope)
    private val observer: Observer<AuthenticationResult> = mock()

    @get:Rule
    val instanceExecutor = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        doNothing().whenever(observer).onChanged(any())
        doNothing().whenever(threeDS).authorizeTransaction(authorizeUrl)
        viewModel.authentication.observeForever(observer)
    }

    @Test
    fun authorizeTransaction_shouldExecute3DS2AuthorizeTransaction() {
        viewModel.authorizeTransaction(authorizeUrl)
        verify(threeDS).authorizeTransaction(authorizeUrl)
    }

    @Test
    fun onUnsupported_when3DSUnsupportedAuthorizationThenSetUnsupportedResult() {
        viewModel.onUnsupported()
        assertEquals(AuthenticationResult.AuthenticationUnsupported, viewModel.authentication.value)
    }

    @Test
    fun onError_when3DSReturnErrorThenSetErrorResult() {
        val error = Exception("Something went wrong.")
        viewModel.onError(error)
        assertEquals(AuthenticationResult.AuthenticationError(error), viewModel.authentication.value)
    }

    @Test
    fun observeChargeStatus_chargeStatueUpdatedFromPendingToSuccessful() = runBlockingTest {
        val pendingToken = Token(id = tokenID, chargeStatus = ChargeStatus.Pending)
        val successfulToken = pendingToken.copy(chargeStatus = ChargeStatus.Successful)
        whenever(client.send(any<Request<Token>>())).thenReturn(pendingToken, successfulToken)

        viewModel.observeChargeStatus()
        coroutineScope.resumeDispatcher()

        assertEquals(AuthenticationResult.AuthenticationCompleted(successfulToken), viewModel.authentication.value)
    }

    @Test
    fun observeChargeStatus_whenReceiveAPIErrorThenReturnAuthenticationError() = runBlockingTest {
        val error = APIError(code = "authentication_failure", message = "Authentication failure.")
        whenever(client.send(any<Request<Token>>())).thenThrow(error)

        viewModel.observeChargeStatus()
        coroutineScope.resumeDispatcher()

        assertEquals(AuthenticationResult.AuthenticationError(error), viewModel.authentication.value)
    }

    @Test
    fun observeChargeStatus_whenReceiveExceptionThenReturnAuthenticationError() = runBlockingTest {
        val error = RuntimeException("Something went wrong.")
        whenever(client.send(any<Request<Token>>())).thenThrow(error)

        viewModel.observeChargeStatus()
        coroutineScope.resumeDispatcher()

        assertEquals(AuthenticationResult.AuthenticationError(error), viewModel.authentication.value)
    }

    @Test
    fun observeChargeStatus_whenReceiveSearchUnavailableErrorThenContinueObserveChargeStatus() = runBlockingTest {
        val error = APIError(code = "search_unavailable", message = "Search token unavailable.")
        val pendingToken = Token(id = tokenID, chargeStatus = ChargeStatus.Pending)
        val successfulToken = pendingToken.copy(chargeStatus = ChargeStatus.Successful)
        whenever(client.send(any<Request<Token>>()))
                .thenThrow(error)
                .thenReturn(pendingToken, successfulToken)

        viewModel.observeChargeStatus()
        coroutineScope.resumeDispatcher()

        assertEquals(AuthenticationResult.AuthenticationCompleted(successfulToken), viewModel.authentication.value)
    }
}
