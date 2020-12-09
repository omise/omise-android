package co.omise.android.ui

import android.app.Activity
import android.content.Intent
import android.util.Base64
import android.view.View
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
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
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.intercepting.SingleActivityFactory
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_AUTHORIZED_URLSTRING
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS
import co.omise.android.R
import co.omise.android.models.ChargeStatus
import co.omise.android.models.Token
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_ERROR
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_PKEY
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_TOKEN
import co.omise.android.ui.OmiseActivity.Companion.EXTRA_TOKEN_OBJECT
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AuthorizingPaymentActivityTest {

    private val authorizeUrl = "https://www.omise.co/pay"
    private val returnUrl = "http://www.example.com"
    private val publicKey = "pkey_test_1234"
    private val tokenID = "tokn_test_1234"
    private val intent = Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
        putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeUrl)
        putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
        putExtra(EXTRA_PKEY, publicKey)
        putExtra(EXTRA_TOKEN, tokenID)
    }

    private val viewModel: AuthorizingPaymentViewModel = mock()
    private val viewModelFactory: AuthorizingPaymentViewModelFactory = mock()
    private val authentication = MutableLiveData<AuthenticationResult>()

    private val activityFactory = object : SingleActivityFactory<AuthorizingPaymentActivity>(AuthorizingPaymentActivity::class.java) {
        override fun create(intent: Intent?): AuthorizingPaymentActivity {
            val activity = AuthorizingPaymentActivity()
            activity.setAuthorizingPaymentViewModelFactory(viewModelFactory)
            return activity
        }
    }

    @get:Rule
    var activityRule = ActivityTestRule(activityFactory, false, false)

    @Before
    fun setUp() {
        whenever(viewModelFactory.create(AuthorizingPaymentViewModel::class.java)).thenReturn(viewModel)
        whenever(viewModel.authentication).thenReturn(authentication)
        doNothing().whenever(viewModel).observeChargeStatus()
        doNothing().whenever(viewModel).cleanup()

        activityRule.launchActivity(intent)
    }

    @Test
    fun onCreate_shouldExecuteAuthorizeTransaction() {
        onView(instanceOf(ProgressBar::class.java)).check(matches(isDisplayed()))
        verify(viewModel).authorizeTransaction(authorizeUrl)
    }

    @Test
    fun fallback3DS1_whenTransactionUse3DS1ThenLoadAuthorizeUrlToWebView() {
        authentication.postValue(AuthenticationResult.AuthenticationUnsupported)

        onView(withId(R.id.authorizing_payment_webview))
                .check(matches(withUrl(authorizeUrl)))
    }

    @Test
    fun activityResultOf3DS1_whenAuthorizationCompletedThenReturnExpectedReturnUrl() {
        authentication.postValue(AuthenticationResult.AuthenticationUnsupported)

        onView(withId(R.id.authorizing_payment_webview))
                .perform(loadUrl(returnUrl))

        val actualResult = activityRule.activityResult
        assertEquals(Activity.RESULT_OK, actualResult.resultCode)
        assertEquals(returnUrl, actualResult.resultData.getStringExtra(AuthorizingPaymentURLVerifier.EXTRA_RETURNED_URLSTRING))
    }

    @Test
    fun authorizationCompleted_returnActivityResultWithToken() {
        val token = Token(id = tokenID, chargeStatus = ChargeStatus.Successful)
        authentication.postValue(AuthenticationResult.AuthenticationCompleted(token))

        val actualResult = activityRule.activityResult
        assertEquals(Activity.RESULT_OK, actualResult.resultCode)
        assertEquals(token, actualResult.resultData.getParcelableExtra(EXTRA_TOKEN_OBJECT))
    }

    @Test
    fun authorizationFailed_returnActivityResultWithErrorMessage() {
        val error = Exception("Somethings went wrong.")
        authentication.postValue(AuthenticationResult.AuthenticationFailure(error))

        val actualResult = activityRule.activityResult
        assertEquals(Activity.RESULT_CANCELED, actualResult.resultCode)
        assertEquals(error.message, actualResult.resultData.getStringExtra(EXTRA_ERROR))
    }

    @Test
    fun activityDestroy_returnCanceledResult() {
        authentication.postValue(AuthenticationResult.AuthenticationUnsupported)

        activityRule.activity.finish()

        val actualResult = activityRule.activityResult
        assertEquals(Activity.RESULT_CANCELED, actualResult.resultCode)
    }

    @Test
    fun webViewDialog_whenJSAlertInvokeThenDisplayAlertDialog() {
        authentication.postValue(AuthenticationResult.AuthenticationUnsupported)

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

        loadData(html)

        onWebView()
                .withElement(findElement(Locator.ID, "button"))
                .check(webMatches(getText(), containsString("Submit")))
                .perform(webClick())

        onView(withText(("Test alert!"))).inRoot(isDialog()).check(matches(isDisplayed()))
    }

    private fun withUrl(url: String): Matcher<View> = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) {
            description?.appendText("with webview url: $url")
        }

        override fun matchesSafely(item: View?): Boolean {
            val webView = item as? WebView ?: return false
            return webView.url == url
        }
    }

    private fun loadUrl(url: String): ViewAction = object : ViewAction {
        override fun getDescription(): String = "WebView load url: $url."

        override fun getConstraints(): Matcher<View> =
                allOf(isAssignableFrom(WebView::class.java))

        override fun perform(uiController: UiController?, view: View?) {
            val webView = view as? WebView ?: return
            webView.webViewClient.shouldOverrideUrlLoading(webView, url)
        }
    }

    private fun loadData(htmlData: String) {
        val webView = activityRule.activity.findViewById<WebView>(R.id.authorizing_payment_webview)
        val encodedHtml = Base64.encodeToString(htmlData.toByteArray(), Base64.NO_PADDING)
        activityRule.activity.runOnUiThread {
            webView.loadData(encodedHtml, "text/html", "base64")
        }
    }
}
