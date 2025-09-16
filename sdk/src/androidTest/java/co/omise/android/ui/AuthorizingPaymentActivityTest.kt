package co.omise.android.ui

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
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
import co.omise.android.extensions.parcelable
import co.omise.android.models.Authentication.AuthenticationStatus
import co.omise.android.ui.AuthorizingPaymentActivity.Companion.EXTRA_AUTHORIZING_PAYMENT_RESULT
import co.omise.android.ui.AuthorizingPaymentActivity.Companion.EXTRA_THREE_DS_REQUESTOR_APP_URL
import co.omise.android.ui.AuthorizingPaymentActivity.Companion.WEBVIEW_CLOSED_RESULT_CODE
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
import org.mockito.kotlin.whenever
import java.util.concurrent.TimeUnit

@LargeTest
@RunWith(AndroidJUnit4::class)
class AuthorizingPaymentActivityTest {
    @get:Rule
    val intentRule = IntentsRule()

    @get:Rule
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    private val authorizeAcsUrl = "https://www.omise.co/authorize?acs=true"
    private val authorizeNonAcsUrl = "https://www.omise.co/authorize?acs=false"
    private val authorizePasskeyUrl = "https://www.omise.co/authorize?signature=test"
    private val authorizePasskeyWithAcsUrl = "https://www.omise.co/authorize?signature=test&acs=true"
    private val returnUrl = "https://www.example.com/complete"
    private val deepLinkAuthorizeUrl = "bankapp://omise.co/authorize?return_uri=sampleapp://omise.co/authorize_return?result=success"
    private val deepLinkReturnUrl = "sampleapp://omise.co/authorize_return?result=success"
    private val threeDSRequestorAppURL = "sampleapp://omise.co/authorize_return"

    private val transaction: Transaction = mock()
    private val progressView: ProgressView = mock()
    private val mockViewModel: AuthorizingPaymentViewModel = mock()
    private val viewModelFactory =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
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

        interceptActivityLifecycle { activity, _ ->
            (activity as? AuthorizingPaymentActivity)?.setViewModelFactory(viewModelFactory)
        }
    }

    @Test
    fun passkeyFlow_whenSignatureParamExistsThenOpenNativeBrowser() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizePasskeyUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            }
        intending(hasData(Uri.parse(authorizePasskeyUrl))).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)

        val activityResult = scenario.result
        assertEquals(Activity.RESULT_OK, activityResult.resultCode)

        intended(
            allOf(
                IntentMatchers.hasAction(Intent.ACTION_VIEW),
                hasData(Uri.parse(authorizePasskeyUrl)),
            ),
        )
    }

    @Test
    fun passkeyFlow_whenSignatureParamExistsWithAcsThenPrioritizePasskey() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizePasskeyWithAcsUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            }
        intending(hasData(Uri.parse(authorizePasskeyWithAcsUrl))).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)

        val activityResult = scenario.result
        assertEquals(Activity.RESULT_OK, activityResult.resultCode)

        // Should open in native browser, not WebView
        intended(
            allOf(
                IntentMatchers.hasAction(Intent.ACTION_VIEW),
                hasData(Uri.parse(authorizePasskeyWithAcsUrl)),
            ),
        )
    }

    @Test
    fun passkeyFlow_whenSignatureParamNotExistsThenFollowNormalFlow() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeNonAcsUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            }
        ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)

        // Should setup WebView instead of opening native browser
        onView(withId(R.id.authorizing_payment_webview))
            .check(matches(isDisplayed()))
            .check(matches(withUrl(authorizeNonAcsUrl)))
    }

    @Test
    fun errorWhenThreeDSRequestorAppURLNotSet() {
        // Create an intent without setting EXTRA_THREE_DS_REQUESTOR_APP_URL
        val intentWithoutThreeDSRequestorAppURL =
            Intent(
                ApplicationProvider.getApplicationContext(),
                AuthorizingPaymentActivity::class.java,
            ).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeNonAcsUrl)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            }

        // Launch the activity
        val scenario =
            ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(
                intentWithoutThreeDSRequestorAppURL,
            )
        val activityResult = scenario.result
        // Check if the received error is as expected
        val expectedError = OmiseException("The threeDSRequestorAppURL must be provided in the intent")
        activityResult.resultData.setExtrasClassLoader(this::class.java.classLoader)
        assertEquals(Activity.RESULT_OK, activityResult.resultCode)
        assertEquals(
            expectedError.message,
            activityResult.resultData.parcelable<AuthorizingPaymentResult.Failure>(
                EXTRA_AUTHORIZING_PAYMENT_RESULT,
            )?.throwable?.message,
        )
    }

    @Test
    fun fallback3DS1_whenAuthenticationStatusIsChallengeV1ThenLoadAuthorizeUrlToWebView() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeAcsUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            }
        ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        authenticationStatus.postValue(AuthenticationStatus.CHALLENGE_V1)

        onView(withId(R.id.authorizing_payment_webview))
            .check(matches(isDisplayed()))
            .check(matches(withUrl(authorizeAcsUrl)))
    }

    @Test
    fun fallbackToWebView_whenOmiseExceptionIsProtocolError() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeAcsUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            }
        ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        val testException = OmiseException(ChallengeStatus.PROTOCOL_ERROR.value)
        error.postValue(testException)
        onView(withId(R.id.authorizing_payment_webview))
            .check(matches(isDisplayed()))
            .check(matches(withUrl(authorizeAcsUrl)))
        onView(withId(R.id.authorizing_payment_webview)).perform(loadUrl(returnUrl))
        onView(withId(R.id.authorizing_payment_webview)).perform(loadUrl(returnUrl))
    }

    @Test
    fun fallbackToWebView_whenOmiseExceptionIsChallengeRuntimeError() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeAcsUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            }
        ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        val testException = OmiseException(ChallengeStatus.RUNTIME_ERROR.value)
        error.postValue(testException)
        onView(withId(R.id.authorizing_payment_webview))
            .check(matches(isDisplayed()))
            .check(matches(withUrl(authorizeAcsUrl)))
        onView(withId(R.id.authorizing_payment_webview)).perform(loadUrl(returnUrl))
    }

    @Test
    fun fallbackToWebView_whenOmiseExceptionIs3DS2InitializationFailed() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeAcsUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            }
        ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        val testException = OmiseException(OmiseSDKError.THREE_DS2_INITIALIZATION_FAILED.value)
        error.postValue(testException)
        onView(withId(R.id.authorizing_payment_webview))
            .check(matches(isDisplayed()))
            .check(matches(withUrl(authorizeAcsUrl)))
        onView(withId(R.id.authorizing_payment_webview)).perform(loadUrl(returnUrl))
    }

    @Test
    fun returnActivityResult_whenWebViewRedirectToReturnUrlThenReturnCompletedResult() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeNonAcsUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            }
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)

        scenario.onActivity { activity -> activity.setTestWebView() }

        onView(withId(R.id.authorizing_payment_webview)).perform(loadUrl(returnUrl))

        val activityResult = scenario.result
        // Due to issue BadParcelableException: ClassNotFoundException when unmarshalling.
        // To workaround this it needs to set classloader explicitly https://github.com/android/android-test/issues/733
        activityResult.resultData.setExtrasClassLoader(this::class.java.classLoader)
        assertEquals(Activity.RESULT_OK, activityResult.resultCode)
        assertEquals(returnUrl, activityResult.resultData.getStringExtra(AuthorizingPaymentURLVerifier.EXTRA_RETURNED_URLSTRING))
        assertEquals(ThreeDS1Completed(returnUrl), activityResult.resultData.parcelable(EXTRA_AUTHORIZING_PAYMENT_RESULT))
    }

    @Test
    fun returnActivityResult_whenAuthenticationStatusIsSuccessThenReturnCompletedResult() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeAcsUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            }
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        authenticationStatus.postValue(AuthenticationStatus.SUCCESS)

        val activityResult = scenario.result
        activityResult.resultData.setExtrasClassLoader(this::class.java.classLoader)
        assertEquals(Activity.RESULT_OK, activityResult.resultCode)
        assertEquals(
            ThreeDS2Completed(TransactionStatus.AUTHENTICATED),
            activityResult.resultData.parcelable(EXTRA_AUTHORIZING_PAYMENT_RESULT),
        )
    }

    @Test
    fun returnActivityResult_whenTransactionStatusIsAuthenticatedThenReturnCompletedResult() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeAcsUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            }
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        transactionStatus.postValue(TransactionStatus.AUTHENTICATED)

        val activityResult = scenario.result
        activityResult.resultData.setExtrasClassLoader(this::class.java.classLoader)
        assertEquals(Activity.RESULT_OK, activityResult.resultCode)
        assertEquals(
            ThreeDS2Completed(TransactionStatus.AUTHENTICATED),
            activityResult.resultData.parcelable(EXTRA_AUTHORIZING_PAYMENT_RESULT),
        )
    }

    @Test
    fun returnActivityResult_whenTransactionStatusIsNotAuthenticatedThenReturnCompletedResult() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeAcsUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            }
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        transactionStatus.postValue(TransactionStatus.NOT_AUTHENTICATED)

        val activityResult = scenario.result
        activityResult.resultData.setExtrasClassLoader(this::class.java.classLoader)
        assertEquals(Activity.RESULT_OK, activityResult.resultCode)
        assertEquals(
            ThreeDS2Completed(TransactionStatus.NOT_AUTHENTICATED),
            activityResult.resultData.parcelable(EXTRA_AUTHORIZING_PAYMENT_RESULT),
        )
    }

    @Test
    fun returnActivityResult_whenAuthenticationStatusIsFailedThenReturnFailureResult() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeAcsUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            }
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        authenticationStatus.postValue(AuthenticationStatus.FAILED)

        val activityResult = scenario.result
        activityResult.resultData.setExtrasClassLoader(this::class.java.classLoader)
        assertEquals(Activity.RESULT_OK, activityResult.resultCode)
        assertEquals(
            AuthenticationStatus.FAILED.message,
            (
                activityResult.resultData.parcelable(
                    EXTRA_AUTHORIZING_PAYMENT_RESULT,
                ) as? AuthorizingPaymentResult.Failure
            )?.throwable?.message,
        )
    }

    @Test
    fun returnActivityResult_whenHasErrorThenReturnFailureResult() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeAcsUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            }
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        val randomError = "Somethings went wrong"
        val testException = OmiseException(randomError)
        error.postValue(testException)

        val activityResult = scenario.result
        activityResult.resultData.setExtrasClassLoader(this::class.java.classLoader)
        assertEquals(Activity.RESULT_OK, activityResult.resultCode)
        assertEquals(
            randomError,
            activityResult.resultData.parcelable<AuthorizingPaymentResult.Failure>(
                EXTRA_AUTHORIZING_PAYMENT_RESULT,
            )?.throwable?.message,
        )
    }

    @Test
    fun activityDestroy_returnCanceledResult() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeAcsUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            }
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        authenticationStatus.postValue(AuthenticationStatus.CHALLENGE_V1)

        scenario.onActivity {
            it.finish()
        }

        assertEquals(Activity.RESULT_CANCELED, scenario.result.resultCode)
    }

    @Test
    fun webViewDialog_whenJSAlertInvokeThenDisplayAlertDialog() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeAcsUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            }
        ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        authenticationStatus.postValue(AuthenticationStatus.CHALLENGE_V1)

        val html =
            """
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
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, deepLinkAuthorizeUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(deepLinkReturnUrl))
            }
        intending(hasData(Uri.parse(deepLinkAuthorizeUrl))).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        scenario.onActivity { activity -> activity.setTestWebView() }
        // load the url again since the trigger is not registered on the creation of the intent due to a know issue
        onView(withId(R.id.authorizing_payment_webview)).perform(loadUrl(deepLinkAuthorizeUrl))
        val activityResult = scenario.result

        intended(
            allOf(
                IntentMatchers.hasAction(Intent.ACTION_VIEW),
                hasData(Uri.parse(deepLinkAuthorizeUrl)),
            ),
        )
    }

    @Test
    fun openDeepLink_whenPressBackOnExternalAppThenReturnResult() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, deepLinkAuthorizeUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(deepLinkReturnUrl))
            }
        intending(hasData(Uri.parse(deepLinkAuthorizeUrl))).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)

        uiDevice.pressBack()

        assertEquals(WEBVIEW_CLOSED_RESULT_CODE, scenario.result.resultCode)
        assertNull(scenario.result.resultData)
    }

    @Test
    fun openDeepLink_whenPressDeepLinkFromWebViewThenOpenExternalApp() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeAcsUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            }
        ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        authenticationStatus.postValue(AuthenticationStatus.CHALLENGE_V1)

        val html =
            """
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
                hasData(Uri.parse(deepLinkAuthorizeUrl)),
            ),
        )
    }

    @Test
    fun progressView_whenLoadingIsTrueThenShowProgressView() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeAcsUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            }
        ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)

        isLoading.postValue(true)
        countingTaskExecutorRule.drainTasks(3, TimeUnit.SECONDS)

        verify(progressView).showProgress()
    }

    @Test
    fun progressView_whenLoadingIsFalseThenDoNothing() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeAcsUrl)
                putExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL, threeDSRequestorAppURL)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            }
        ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)

        isLoading.postValue(false)
        countingTaskExecutorRule.drainTasks(3, TimeUnit.SECONDS)

        // Closing the transaction will also hide the progress view. So we don't need to call hideProgress() here.
        verify(progressView, never()).showProgress()
        verify(progressView, never()).hideProgress()
    }
}
