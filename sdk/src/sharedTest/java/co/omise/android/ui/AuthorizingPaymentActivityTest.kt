package co.omise.android.ui

import android.app.Activity
import android.content.Intent
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.model.Atoms.getCurrentUrl
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_AUTHORIZED_URLSTRING
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS
import co.omise.android.R
import org.hamcrest.CoreMatchers.containsString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.setupActivity
import org.robolectric.RobolectricTestRunner

@RunWith(AndroidJUnit4::class)
class AuthorizingPaymentActivityTest {

    private val intent = Intent(ApplicationProvider.getApplicationContext(), AuthorizingPaymentActivity::class.java).apply {
        putExtra(EXTRA_AUTHORIZED_URLSTRING, "https://pay.omise.co/offsites/ofsp_test_5gfea5g4cg4trkoa4bo/pay")
        putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf("http://www.example.com"))
    }

    @Test
    fun onCreate_validIntent() {
        val scenario = launch<AuthorizingPaymentActivity>(intent)
//        ApplicationProvider.getApplicationContext<AuthorizingPaymentSupportActivity>().run
//        onWebView(withId(R.id.authorizing_payment_webview))
//                .check(webMatches(getCurrentUrl(), containsString("http://www.example.com")))

        val result = scenario.result
        assertEquals(Activity.RESULT_OK, result.resultCode)
    }

    @Test
    fun onCreate_invalidIntent() {}

//    @Test
//    fun displayAuthorize() {
//        val scenario = launch<AuthorizingPaymentSupportActivity>(intent)
//
//        onView(withId(R.id.authorizing_payment_webview))
//        val result = scenario.result
//        assertEquals(Activity.RESULT_OK, result.resultCode)
//        assertTrue(true)
//    }
//
//    @Test(expected = IllegalArgumentException::class)
//    fun startActivityWithoutIntent() {
//        val scenario = launch(AuthorizingPaymentSupportActivity::class.java)
//    }
}
