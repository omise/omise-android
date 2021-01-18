package co.omise.android.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import co.omise.android.threeds.ThreeDS
import co.omise.android.threeds.data.models.TransactionStatus
import co.omise.android.threeds.events.CompletionEvent
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class AuthorizingPaymentViewModelTest {

    private val threeDS: ThreeDS = mock()
    private val sdkTransID = "skts_test_1234"

    private val authorizeUrl = "https://www.omise.co/pay"
    private val testDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testDispatcher)
    private val viewModel = spy(AuthorizingPaymentViewModel(threeDS))
    private val observer: Observer<AuthenticationResult> = mock()

    @get:Rule
    val instanceExecutor = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        doNothing().whenever(observer).onChanged(any())
        doNothing().whenever(threeDS).authorizeTransaction(authorizeUrl)
        viewModel.setCoroutineScope(testCoroutineScope)
        viewModel.authentication.observeForever(observer)
    }

    @Test
    fun authorizeTransaction_shouldExecute3DS2AuthorizeTransaction() {
        viewModel.authorizeTransaction(authorizeUrl)
        verify(threeDS).authorizeTransaction(authorizeUrl)
    }

    @Test
    fun onCompleted_shouldExecuteObserveChargeStatus() {
        val completionEvent = CompletionEvent(sdkTransID,TransactionStatus.AUTHENTICATED)
        viewModel.onCompleted(completionEvent)

        assertEquals(AuthenticationResult.AuthenticationCompleted(completionEvent), viewModel.authentication.value)
    }

    @Test
    fun onUnsupported_when3DSUnsupportedAuthorizationThenSetUnsupportedResult() {
        viewModel.onUnsupported()
        assertEquals(AuthenticationResult.AuthenticationUnsupported, viewModel.authentication.value)
    }

    @Test
    fun onError_when3DSReturnErrorThenSetErrorResult() {
        val error = Exception("Something went wrong.")
        viewModel.onFailure(error)
        assertEquals(AuthenticationResult.AuthenticationFailure(error), viewModel.authentication.value)
    }

}
