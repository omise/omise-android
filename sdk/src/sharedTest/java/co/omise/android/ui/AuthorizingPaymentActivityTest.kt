package co.omise.android.ui

import android.app.Activity
import android.content.Intent
import android.view.View
import android.webkit.WebView
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.pressBack
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_AUTHORIZED_URLSTRING
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS
import co.omise.android.R
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthorizingPaymentActivityTest {

    private val TEST_AUTHORIZED_URL = "https://pay.omise.co/offsites/ofsp_test_5gfea5g4cg4trkoa4bo/pay"
    private val TEST_RETURN_URL = "http://www.example.com"
    private val intent = Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
        putExtra(EXTRA_AUTHORIZED_URLSTRING, TEST_AUTHORIZED_URL)
        putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(TEST_RETURN_URL))
    }

    @Test
    fun onCreate_loadAuthorizeUrl() {
        val scenario = launch<AuthorizingPaymentActivity>(intent)

        onView(withId(R.id.authorizing_payment_webview))
                .check(matches(withUrl(TEST_AUTHORIZED_URL)))
    }

    @Test
    fun verifyUrl_matchedWithExpectedReturnUrl() {
        val scenario = launch<AuthorizingPaymentActivity>(intent)

        onView(withId(R.id.authorizing_payment_webview))
                .perform(loadUrl(TEST_RETURN_URL))

        val result = scenario.result
        assertEquals(Activity.RESULT_OK, result.resultCode)
        assertEquals(TEST_RETURN_URL, result.resultData.getStringExtra(AuthorizingPaymentURLVerifier.EXTRA_RETURNED_URLSTRING))
    }

    @Test
    fun activityDestroy_returnCanceledResult() {
        val scenario = launch<AuthorizingPaymentActivity>(intent)

        pressBackUnconditionally()

        val result = scenario.result
        assertEquals(Activity.RESULT_CANCELED, result.resultCode)
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
}
