package co.omise.android.extensions

import co.omise.android.api.Request
import co.omise.android.models.APIError
import co.omise.android.models.ChargeStatus
import co.omise.android.models.Token
import co.omise.android.threeds.data.models.TransactionStatus
import co.omise.android.threeds.events.CompletionEvent
import co.omise.android.ui.AuthenticationResult
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Test

class ClientExtensionsKtTest {

//    @Test
//    fun observeChargeStatus_chargeStatueUpdatedFromPendingToSuccessful() = runBlockingTest {
//        val completionEvent = CompletionEvent(sdkTransID, TransactionStatus.AUTHENTICATED)
//        val pendingToken = Token(id = tokenID, chargeStatus = ChargeStatus.Pending)
//        val successfulToken = pendingToken.copy(chargeStatus = ChargeStatus.Successful)
//        whenever(client.send(any<Request<Token>>())).thenReturn(pendingToken, successfulToken)
//
////        viewModel.observeChargeStatus()
//        testCoroutineScope.resumeDispatcher()
//
////        assertEquals(AuthenticationResult.AuthenticationCompleted(successfulToken), viewModel.authentication.value)
//    }
//
//    @Test
//    fun observeChargeStatus_whenReceiveAPIErrorThenReturnAuthenticationFailure() = runBlockingTest {
//        val error = APIError(code = "authentication_failure", message = "Authentication failure.")
//        whenever(client.send(any<Request<Token>>())).thenThrow(error)
//
////        viewModel.observeChargeStatus()
//        testCoroutineScope.resumeDispatcher()
//
//        assertEquals(AuthenticationResult.AuthenticationFailure(error), viewModel.authentication.value)
//    }
//
//    @Test
//    fun observeChargeStatus_whenReceiveExceptionThenReturnAuthenticationFailure() = runBlockingTest {
//        val error = RuntimeException("Something went wrong.")
//        whenever(client.send(any<Request<Token>>())).thenThrow(error)
//
////        viewModel.observeChargeStatus()
//        testCoroutineScope.resumeDispatcher()
//
//        assertEquals(AuthenticationResult.AuthenticationFailure(error), viewModel.authentication.value)
//    }
//
//    @Test
//    fun observeChargeStatus_whenReceiveSearchUnavailableErrorThenContinueObserveChargeStatus() = runBlockingTest {
//        val error = APIError(code = "search_unavailable", message = "Search token unavailable.")
//        val pendingToken = Token(id = tokenID, chargeStatus = ChargeStatus.Pending)
//        val successfulToken = pendingToken.copy(chargeStatus = ChargeStatus.Successful)
//        whenever(client.send(any<Request<Token>>()))
//                .thenThrow(error)
//                .thenReturn(pendingToken, successfulToken)
//
////        viewModel.observeChargeStatus()
//        testCoroutineScope.resumeDispatcher()
//
//        assertEquals(AuthenticationResult.AuthenticationCompleted(successfulToken), viewModel.authentication.value)
//    }
}