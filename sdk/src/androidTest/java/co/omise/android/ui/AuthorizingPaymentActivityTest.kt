package co.omise.android.ui

import android.app.Activity
import android.content.Intent
import android.util.Base64
import android.view.View
import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
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
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_AUTHORIZED_URLSTRING
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS
import co.omise.android.R
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AuthorizingPaymentActivityTest {

    private val TEST_AUTHORIZED_URL = "https://www.omise.co/pay"
    private val TEST_RETURN_URL = "http://www.example.com"
    private val intent = Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
        putExtra(EXTRA_AUTHORIZED_URLSTRING, TEST_AUTHORIZED_URL)
        putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(TEST_RETURN_URL))
    }
    private lateinit var scenario: ActivityScenario<AuthorizingPaymentActivity>

    @Before
    fun setUp() {
        scenario = launch(intent)
    }

    @Test
    fun onCreate_loadAuthorizeUrl() {
        onView(withId(R.id.authorizing_payment_webview))
                .check(matches(withUrl(TEST_AUTHORIZED_URL)))
    }

    @Test
    fun verifyUrl_matchedWithExpectedReturnUrl() {
        onView(withId(R.id.authorizing_payment_webview))
                .perform(loadUrl(TEST_RETURN_URL))

        val result = scenario.result
        assertEquals(Activity.RESULT_OK, result.resultCode)
        assertEquals(TEST_RETURN_URL, result.resultData.getStringExtra(AuthorizingPaymentURLVerifier.EXTRA_RETURNED_URLSTRING))
    }

    @Test
    fun activityDestroy_returnCanceledResult() {
        pressBackUnconditionally()

        val result = scenario.result
        assertEquals(Activity.RESULT_CANCELED, result.resultCode)
    }

    @Test
    fun webViewDialog_whenJSAlertInvokeThenDisplayAlertDialog() {
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
        scenario.onActivity {
            val webView = it.findViewById<WebView>(R.id.authorizing_payment_webview)
            val encodedHtml = Base64.encodeToString(htmlData.toByteArray(), Base64.NO_PADDING)
            webView.loadData(encodedHtml, "text/html", "base64")
        }
    }
}
