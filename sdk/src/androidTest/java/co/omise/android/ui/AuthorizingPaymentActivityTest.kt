package co.omise.android.ui

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import android.view.WindowManager
import android.widget.ProgressBar
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
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import androidx.test.uiautomator.UiDevice
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_AUTHORIZED_URLSTRING
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS
import co.omise.android.OmiseException
import co.omise.android.R
import co.omise.android.threeds.data.models.ErrorCode
import co.omise.android.threeds.data.models.MessageType
import co.omise.android.threeds.data.models.TransactionStatus
import co.omise.android.threeds.events.ErrorMessage
import co.omise.android.threeds.events.ProtocolErrorEvent
import co.omise.android.threeds.events.RuntimeErrorEvent
import co.omise.android.ui.AuthenticationResult.AuthenticationUnsupported
import co.omise.android.ui.AuthorizingPaymentActivity.Companion.EXTRA_AUTHORIZING_PAYMENT_RESULT
import co.omise.android.utils.loadHtml
import co.omise.android.utils.loadUrl
import co.omise.android.utils.withUrl
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


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

    private val mockViewModel: AuthorizingPaymentViewModel = mock()
    private val viewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return mockViewModel as T
        }
    }

    private val authentication = MutableLiveData<AuthenticationResult>()

    private val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Before
    fun setUp() {
        whenever(mockViewModel.authentication).thenReturn(authentication)
        doNothing().whenever(mockViewModel).cleanup()

        ActivityLifecycleMonitorRegistry.getInstance().addLifecycleCallback { activity, stage ->
            if (stage == Stage.PRE_ON_CREATE) {
                (activity as? AuthorizingPaymentActivity)?.setViewModelFactory(viewModelFactory)
            }
        }
    }

    @Test
    @Ignore("Due to switching off 3DS SDK feature, so this test is not valid")
    fun onCreate_shouldExecuteAuthorizeTransaction() {
        ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        onView(instanceOf(ProgressBar::class.java)).check(matches(isDisplayed()))
        verify(mockViewModel).authorizeTransaction(authorizeUrl)
    }

    @Test
    @Ignore("Due to switching off 3DS SDK feature, so this test is not valid")
    fun fallback3DS1_whenTransactionUse3DS1ThenLoadAuthorizeUrlToWebView() {
        ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        authentication.postValue(AuthenticationUnsupported)

        onView(withId(R.id.authorizing_payment_webview))
            .check(matches(isDisplayed()))
            .check(matches(withUrl(authorizeUrl)))
    }

    @Test
    @Ignore("Due to switching off 3DS SDK feature, so this test is not valid")
    fun activityResultOf3DS1_whenAuthorizationCompletedThenReturnExpectedReturnUrl() {
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        authentication.postValue(AuthenticationUnsupported)

        onView(withId(R.id.authorizing_payment_webview)).perform(loadUrl(returnUrl))

        val actualResult = scenario.result
        assertEquals(Activity.RESULT_OK, actualResult.resultCode)
        assertEquals(returnUrl, actualResult.resultData.getStringExtra(AuthorizingPaymentURLVerifier.EXTRA_RETURNED_URLSTRING))
        assertEquals(
            AuthorizingPaymentResult.ThreeDS1Completed(returnUrl),
            actualResult.resultData.getParcelableExtra(EXTRA_AUTHORIZING_PAYMENT_RESULT)
        )
    }

    @Test
    @Ignore("Due to switching off 3DS SDK feature, so this test is not valid")
    fun authorizationCompleted_returnActivityResultWith3DS2CompletedResult() {
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        val completionEvent = co.omise.android.threeds.events.CompletionEvent("test_id_1234", TransactionStatus.AUTHENTICATED)
        authentication.postValue(AuthenticationResult.AuthenticationCompleted(completionEvent))

        val actualResult = scenario.result
        assertEquals(Activity.RESULT_OK, actualResult.resultCode)
        assertEquals(
            AuthorizingPaymentResult.ThreeDS2Completed("test_id_1234", "Y"),
            actualResult.resultData.getParcelableExtra(EXTRA_AUTHORIZING_PAYMENT_RESULT)
        )
    }

    @Test
    @Ignore("Due to switching off 3DS SDK feature, so this test is not valid")
    fun authorizationFailed_returnActivityResultWithErrorMessage() {
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        val testException = Exception("Somethings went wrong.")
        authentication.postValue(AuthenticationResult.AuthenticationFailure(testException))

        val actualResult = scenario.result
        val actualFailure = actualResult.resultData.getParcelableExtra<AuthorizingPaymentResult.Failure>(EXTRA_AUTHORIZING_PAYMENT_RESULT)!!
        assertEquals(Activity.RESULT_OK, actualResult.resultCode)
        assertTrue(actualFailure.throwable is OmiseException)
        assertEquals("3D Secure authorization failed: Somethings went wrong.", actualFailure.throwable.message)
    }

    @Test
    @Ignore("Due to switching off 3DS SDK feature, so this test is not valid")
    fun authorizationFailed_protocolError() {
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        val error = ProtocolErrorEvent(
            transactionId = "1234",
            errorMessage = ErrorMessage(
                messageType = MessageType.ERROR,
                messageVersion = "2.2.0",
                errorCode = ErrorCode.InvalidFormat,
                errorDetail = "sdkTransID is invalided UUID format.",
                errorDescription = "sdkTransID is invalided UUID format.",
            )
        )
        authentication.postValue(AuthenticationResult.AuthenticationFailure(error))

        val actualResult = scenario.result
        val actualFailure = actualResult.resultData.getParcelableExtra<AuthorizingPaymentResult.Failure>(EXTRA_AUTHORIZING_PAYMENT_RESULT)!!
        assertEquals(Activity.RESULT_OK, actualResult.resultCode)
        assertTrue(actualFailure.throwable is OmiseException)
        assertEquals("3D Secure authorization failed: protocol error.", actualFailure.throwable.message)
        assertEquals(
            """
                    errorCode=203,
                    errorDetail=sdkTransID is invalided UUID format.,
                    errorDescription=sdkTransID is invalided UUID format.,
                """.trimIndent(),
            actualFailure.throwable.cause!!.message
        )
    }

    @Test
    @Ignore("Due to switching off 3DS SDK feature, so this test is not valid")
    fun authorizationFailed_runtimeError() {
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        val error = RuntimeErrorEvent(
            errorCode = "1234",
            errorMessage = "Something went wrong."
        )
        authentication.postValue(AuthenticationResult.AuthenticationFailure(error))

        val actualResult = scenario.result
        val actualFailure = actualResult.resultData.getParcelableExtra<AuthorizingPaymentResult.Failure>(EXTRA_AUTHORIZING_PAYMENT_RESULT)!!
        assertEquals(Activity.RESULT_OK, actualResult.resultCode)
        assertTrue(actualFailure.throwable is OmiseException)
        assertEquals("3D Secure authorization failed: runtime error.", actualFailure.throwable.message)
        assertEquals("Something went wrong.", actualFailure.throwable.cause!!.message)
    }

    @Test
    fun activityDestroy_returnCanceledResult() {
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        authentication.postValue(AuthenticationUnsupported)

        scenario.onActivity {
            it.finish()
        }

        assertEquals(Activity.RESULT_CANCELED, scenario.result.resultCode)
    }

    @Test
    fun webViewDialog_whenJSAlertInvokeThenDisplayAlertDialog() {
        ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        authentication.postValue(AuthenticationUnsupported)

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
    fun flagSecure_activityShouldContainFlagSecureInAttributes() {
        val scenario = ActivityScenario.launchActivityForResult<AuthorizingPaymentActivity>(intent)
        scenario.onActivity {
            assertEquals(WindowManager.LayoutParams.FLAG_SECURE, it.window.attributes.flags and WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
}
