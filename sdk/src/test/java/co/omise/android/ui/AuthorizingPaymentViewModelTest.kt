package co.omise.android.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import co.omise.android.api.Client
import co.omise.android.threeds.ThreeDS
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
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
}
