package co.omise.android.ui

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.intent.rule.IntentsRule
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.DriverAtoms.getText
import androidx.test.espresso.web.webdriver.DriverAtoms.webClick
import androidx.test.espresso.web.webdriver.Locator
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_AUTHORIZED_URLSTRING
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS
import co.omise.android.OmiseException
import co.omise.android.R
import co.omise.android.models.Authentication.AuthenticationStatus
import co.omise.android.ui.AuthorizingPaymentActivity.Companion.EXTRA_AUTHORIZING_PAYMENT_RESULT
import co.omise.android.ui.AuthorizingPaymentResult.ThreeDS1Completed
import co.omise.android.ui.AuthorizingPaymentResult.ThreeDS2Completed
import co.omise.android.utils.interceptActivityLifecycle
import co.omise.android.utils.loadHtml
import co.omise.android.utils.loadUrl
import co.omise.android.utils.withUrl
import com.netcetera.threeds.sdk.api.transaction.Transaction
import com.netcetera.threeds.sdk.api.ui.ProgressView
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import java.util.concurrent.TimeUnit


@LargeTest
@RunWith(AndroidJUnit4::class)
class AuthorizingPaymentActivityTest {
    @get:Rule
    val intentRule = IntentsRule()

    @get:Rule
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    private val authorizeUrl = "https://www.omise.co/pay"
    private val returnUrl = "http://www.example.com"
    private val deepLinkAuthorizeUrl = "bankapp://omise.co/authorize?return_uri=sampleapp://omise.co/authorize_return?result=success"
    private val deepLinkReturnUrl = "sampleapp://omise.co/authorize_return?result=success"
    private val intent = Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
        putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeUrl)
        putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
    }

    private val transaction: Transaction = mock()
    private val progressView: ProgressView = mock()
    private val mockViewModel: AuthorizingPaymentViewModel = mock()
    private val viewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return mockViewModel as T
        }
    }

    private val authenticationStatus = MutableLiveData<AuthenticationStatus>()
    private val isLoading = MutableLiveData<Boolean>()
    private val transactionStatus = MutableLiveData<TransactionStatus>()
    private val error = MutableLiveData<OmiseException>()

    private val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Before
    fun setUp() {
        whenever(mockViewModel.authenticationStatus).thenReturn(authenticationStatus)
        whenever(mockViewModel.isLoading).thenReturn(isLoading)
        whenever(mockViewModel.error).thenReturn(error)
        whenever(mockViewModel.transactionStatus).thenReturn(transactionStatus)
        whenever(mockViewModel.getTransaction()).thenReturn(transaction)
        whenever(transaction.getProgressView(any())).thenReturn(progressView)
        doNothing().whenever(mockViewModel).cleanup()

        interceptActivityLifecycle { activity, _ ->
            (activity as? AuthorizingPaymentActivity)?.setViewModelFactory(viewModelFactory)
        }
    }

    @Test
    fun fallback3DS1_whenAuthenticationStatusIsChallengeV1ThenLoadAuthorizeUrlToWebView() {
        ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        authenticationStatus.postValue(AuthenticationStatus.CHALLENGE_V1)

        onView(withId(R.id.authorizing_payment_webview))
            .check(matches(isDisplayed()))
            .check(matches(withUrl(authorizeUrl)))
    }

    @Test
    fun returnActivityResult_whenWebViewRedirectToReturnUrlThenReturnCompletedResult() {
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        authenticationStatus.postValue(AuthenticationStatus.CHALLENGE_V1)

        onView(withId(R.id.authorizing_payment_webview)).perform(loadUrl(returnUrl))

        val activityResult = scenario.result
        // Due to issue BadParcelableException: ClassNotFoundException when unmarshalling.
        // To workaround this it needs to set classloader explicitly https://github.com/android/android-test/issues/733
        activityResult.resultData.setExtrasClassLoader(this::class.java.classLoader)
        assertEquals(Activity.RESULT_OK, activityResult.resultCode)
        assertEquals(returnUrl, activityResult.resultData.getStringExtra(AuthorizingPaymentURLVerifier.EXTRA_RETURNED_URLSTRING))
        assertEquals(ThreeDS1Completed(returnUrl), activityResult.resultData.getParcelableExtra(EXTRA_AUTHORIZING_PAYMENT_RESULT))
    }

    @Test
    fun returnActivityResult_whenAuthenticationStatusIsSuccessThenReturnCompletedResult() {
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        authenticationStatus.postValue(AuthenticationStatus.SUCCESS)

        val activityResult = scenario.result
        activityResult.resultData.setExtrasClassLoader(this::class.java.classLoader)
        assertEquals(Activity.RESULT_OK, activityResult.resultCode)
        assertEquals(
            ThreeDS2Completed(TransactionStatus.AUTHENTICATED),
            activityResult.resultData.getParcelableExtra(EXTRA_AUTHORIZING_PAYMENT_RESULT)
        )
    }

    @Test
    fun returnActivityResult_whenTransactionStatusIsAuthenticatedThenReturnCompletedResult() {
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        transactionStatus.postValue(TransactionStatus.AUTHENTICATED)

        val activityResult = scenario.result
        activityResult.resultData.setExtrasClassLoader(this::class.java.classLoader)
        assertEquals(Activity.RESULT_OK, activityResult.resultCode)
        assertEquals(
            ThreeDS2Completed(TransactionStatus.AUTHENTICATED),
            activityResult.resultData.getParcelableExtra(EXTRA_AUTHORIZING_PAYMENT_RESULT)
        )
    }

    @Test
    fun returnActivityResult_whenTransactionStatusIsNotAuthenticatedThenReturnCompletedResult() {
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        transactionStatus.postValue(TransactionStatus.NOT_AUTHENTICATED)

        val activityResult = scenario.result
        activityResult.resultData.setExtrasClassLoader(this::class.java.classLoader)
        assertEquals(Activity.RESULT_OK, activityResult.resultCode)
        assertEquals(
            ThreeDS2Completed(TransactionStatus.NOT_AUTHENTICATED),
            activityResult.resultData.getParcelableExtra(EXTRA_AUTHORIZING_PAYMENT_RESULT)
        )
    }

    @Test
    fun returnActivityResult_whenAuthenticationStatusIsFailedThenReturnFailureResult() {
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        authenticationStatus.postValue(AuthenticationStatus.FAILED)

        val activityResult = scenario.result
        activityResult.resultData.setExtrasClassLoader(this::class.java.classLoader)
        assertEquals(Activity.RESULT_OK, activityResult.resultCode)
        assertEquals(
            "Authentication failed.",
            (activityResult.resultData.getParcelableExtra(EXTRA_AUTHORIZING_PAYMENT_RESULT) as? AuthorizingPaymentResult.Failure)?.throwable?.message
        )
    }

    @Test
    fun returnActivityResult_whenHasErrorThenReturnFailureResult() {
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        val testException = OmiseException("Somethings went wrong.")
        error.postValue(testException)

        val activityResult = scenario.result
        activityResult.resultData.setExtrasClassLoader(this::class.java.classLoader)
        assertEquals(Activity.RESULT_OK, activityResult.resultCode)
        assertEquals(
            "Somethings went wrong.",
            activityResult.resultData.getParcelableExtra<AuthorizingPaymentResult.Failure>(EXTRA_AUTHORIZING_PAYMENT_RESULT)?.throwable?.message
        )
    }

    @Test
    fun activityDestroy_returnCanceledResult() {
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        authenticationStatus.postValue(AuthenticationStatus.CHALLENGE_V1)

        scenario.onActivity {
            it.finish()
        }

        assertEquals(Activity.RESULT_CANCELED, scenario.result.resultCode)
    }

    @Test
    fun webViewDialog_whenJSAlertInvokeThenDisplayAlertDialog() {
        ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        authenticationStatus.postValue(AuthenticationStatus.CHALLENGE_V1)

        val html = """
            <!DOCTYPE html>
            <html>
            <body>
            <p>Test alert().</p>
            <button onclick="setTimeout(displayAlert, 100);" id="button">Submit</button>
            <script>
            function displayAlert() {
              alert("Test alert!");
            }
            </script>
            </body>
            </html>
       """.trimIndent()
        onView(withId(R.id.authorizing_payment_webview)).perform(loadHtml(html))
        onWebView()
            .withElement(findElement(Locator.ID, "button"))
            .check(webMatches(getText(), containsString("Submit")))
            .perform(webClick())

        onView(withText(("Test alert!"))).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText("OK")).perform(click())
    }

    @Test
    fun openDeepLink_whenAuthorizeUriIsDeepLinkThenOpenExternalApp() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
            putExtra(EXTRA_AUTHORIZED_URLSTRING, deepLinkAuthorizeUrl)
            putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(deepLinkReturnUrl))
        }
        intending(hasData(Uri.parse(deepLinkAuthorizeUrl))).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
        ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)

        intended(
            allOf(
                IntentMatchers.hasAction(Intent.ACTION_VIEW),
                hasData(Uri.parse(deepLinkAuthorizeUrl)),
            )
        )
    }

    @Test
    fun openDeepLink_whenPressBackOnExternalAppThenReturnResult() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
            putExtra(EXTRA_AUTHORIZED_URLSTRING, deepLinkAuthorizeUrl)
            putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(deepLinkReturnUrl))
        }
        intending(hasData(Uri.parse(deepLinkAuthorizeUrl))).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)

        uiDevice.pressBack()

        assertEquals(Activity.RESULT_OK, scenario.result.resultCode)
        assertNull(scenario.result.resultData)
    }

    @Test
    fun openDeepLink_whenPressDeepLinkFromWebViewThenOpenExternalApp() {
        ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        authenticationStatus.postValue(AuthenticationStatus.CHALLENGE_V1)

        val html = """
                <!DOCTYPE html>
                <html lang="en">
                  <head>
                    <meta charset="UTF-8" />
                    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                    <title>Test</title>
                  </head>
                  <body>
                    <a
                      href="$deepLinkAuthorizeUrl"
                      id="deepLinkButton"
                    >
                      Open bank app
                    </a>
                  </body>
                </html>
            """.trimIndent()
        onView(withId(R.id.authorizing_payment_webview)).perform(loadHtml(html))

        onWebView(withId(R.id.authorizing_payment_webview))
            .withElement(findElement(Locator.ID, "deepLinkButton"))
            .check(webMatches(getText(), containsString("Open bank app")))
            .perform(webClick())

        intended(
            allOf(
                IntentMatchers.hasAction(Intent.ACTION_VIEW),
                hasData(Uri.parse(deepLinkAuthorizeUrl))
            )
        )
    }

    @Test
    fun progressView_whenLoadingIsTrueThenShowProgressView() {
        ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)

        isLoading.postValue(true)
        countingTaskExecutorRule.drainTasks(3, TimeUnit.SECONDS)

        verify(progressView).showProgress()
    }

    @Test
    fun progressView_whenLoadingIsFalseThenDoNothing() {
        ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)

        isLoading.postValue(false)
        countingTaskExecutorRule.drainTasks(3, TimeUnit.SECONDS)

        // Closing the transaction will also hide the progress view. So we don't need to call hideProgress() here.
        verify(progressView, never()).showProgress()
        verify(progressView, never()).hideProgress()
    }
}
