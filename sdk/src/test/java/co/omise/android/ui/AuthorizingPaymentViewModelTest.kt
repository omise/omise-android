package co.omise.android.ui

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.platform.app.InstrumentationRegistry
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.ThreeDS2ServiceWrapper
import co.omise.android.api.Client
import co.omise.android.api.Request
import co.omise.android.models.Authentication
import co.omise.android.threeds.ThreeDS
import co.omise.android.threeds.data.models.TransactionStatus
import co.omise.android.threeds.events.CompletionEvent
import com.netcetera.threeds.sdk.ThreeDS2ServiceInstance
import com.netcetera.threeds.sdk.api.ThreeDS2Service
import com.netcetera.threeds.sdk.api.transaction.AuthenticationRequestParameters
import com.netcetera.threeds.sdk.api.transaction.Transaction
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.Mockito.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID


@ExperimentalCoroutinesApi
class AuthorizingPaymentViewModelTest {

    private val threeDS: ThreeDS = mock()
    private val client: Client = mock()
    private val urlVerifier: AuthorizingPaymentURLVerifier = mock()

    //    private val threeDS2Service: ThreeDS2Service = mock()
    private val threeDS2Service: ThreeDS2ServiceWrapper = mock()
    private val transaction: Transaction = mock()
    private val sdkTransID = "skts_test_1234"

    private val authorizeUrl = "https://www.omise.co/pay"
    private val testDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testDispatcher)
    private lateinit var viewModel: AuthorizingPaymentViewModel
    private val observer: Observer<AuthenticationResult> = mock()

    @get:Rule
    val instanceExecutor = InstantTaskExecutorRule()

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
        doNothing().whenever(observer).onChanged(any())
        doNothing().whenever(threeDS).authorizeTransaction(authorizeUrl)
        whenever(urlVerifier.authorizedURLString).thenReturn("https://www.omise.co/pay")
        whenever(urlVerifier.verifyExternalURL()).thenReturn(false)

        doNothing().whenever(threeDS2Service).initialize()
        whenever(threeDS2Service.createTransaction(any(), any())).thenReturn(transaction)
        whenever(transaction.authenticationRequestParameters).thenReturn(authenticationParams)

        viewModel = AuthorizingPaymentViewModel(threeDS, client, urlVerifier, threeDS2Service)
//        viewModel.authentication.observeForever(observer)
    }

    @Test
    fun initialize3DSTransaction_shouldInitialize3DS2Service() = runBlockingTest {
        verify(threeDS2Service).initialize()
        verify(client).send(any<Request<Authentication>>())
    }

//    @Test
//    fun authorizeTransaction_shouldExecute3DS2AuthorizeTransaction() {
//        viewModel.authorizeTransaction(authorizeUrl)
//        verify(threeDS).authorizeTransaction(authorizeUrl)
//    }

//    @Test
//    fun onCompleted_shouldExecuteObserveChargeStatus() {
//        val completionEvent = CompletionEvent(sdkTransID, TransactionStatus.AUTHENTICATED)
//        viewModel.onCompleted(completionEvent)
//
//        assertEquals(AuthenticationResult.AuthenticationCompleted(completionEvent), viewModel.authentication.value)
//    }
//
//    @Test
//    fun onUnsupported_when3DSUnsupportedAuthorizationThenSetUnsupportedResult() {
//        viewModel.onUnsupported()
//        assertEquals(AuthenticationResult.AuthenticationUnsupported, viewModel.authentication.value)
//    }
//
//    @Test
//    fun onError_when3DSReturnErrorThenSetErrorResult() {
//        val error = Exception("Something went wrong.")
//        viewModel.onFailure(error)
//        assertEquals(AuthenticationResult.AuthenticationFailure(error), viewModel.authentication.value)
//    }

}
