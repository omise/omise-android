package co.omise.android.ui

import android.content.Intent
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.ui.AuthorizingPaymentActivity.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.IllegalArgumentException

@RunWith(AndroidJUnit4::class)
class AuthorizingPaymentURLVerifierTest {

    @Test
    fun createInstance_createInstanceWithIntent() {
        val intent = Intent().apply {
            putExtra(EXTRA_AUTHORIZED_URLSTRING, "https://pay.omise.co/offsites/ofsp_test_5gfea5g4cg4trkoa4bo/pay")
            putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf("http://www.example.com"))
        }
        val verifier = AuthorizingPaymentURLVerifier(intent)

        assertEquals(Uri.parse("https://pay.omise.co/offsites/ofsp_test_5gfea5g4cg4trkoa4bo/pay"), verifier.authorizedURL)
        assertEquals(listOf(Uri.parse("http://www.example.com")), verifier.expectedReturnURLPatterns)
    }

    @Test
    fun createInstance_createInstanceWithAuthorizedUrlAndExpectedUrlPatterns() {
        val verifier = AuthorizingPaymentURLVerifier(
                Uri.parse("https://pay.omise.co/offsites/ofsp_test_5gfea5g4cg4trkoa4bo/pay"),
                listOf(Uri.parse("http://www.example.com"))
        )

        assertEquals(Uri.parse("https://pay.omise.co/offsites/ofsp_test_5gfea5g4cg4trkoa4bo/pay"), verifier.authorizedURL)
        assertEquals(listOf(Uri.parse("http://www.example.com")), verifier.expectedReturnURLPatterns)
    }

    @Test(expected = IllegalArgumentException::class)
    fun createInstance_createInstanceWithoutAuthorizedUrl() {
        val intent = Intent().apply {
            putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf("http://www.example.com"))
        }
        val verifier = AuthorizingPaymentURLVerifier(intent)
    }

    @Test(expected = IllegalArgumentException::class)
    fun createInstance_createInstanceWithoutReturnUrls() {
        val intent = Intent().apply {
            putExtra(EXTRA_AUTHORIZED_URLSTRING, "https://pay.omise.co/offsites/ofsp_test_5gfea5g4cg4trkoa4bo/pay")
        }
        val verifier = AuthorizingPaymentURLVerifier(intent)
    }

    @Test
    fun verifyURL_urlWasVerified() {
        val verifier = AuthorizingPaymentURLVerifier(
                Uri.parse("https://pay.omise.co/offsites/ofsp_test_5gfea5g4cg4trkoa4bo/pay"),
                listOf(Uri.parse("http://www.example.com"))
        )

        val wasVerified = verifier.verifyURL(Uri.parse("http://www.example.com"))

        assertTrue(wasVerified)
    }

    @Test
    fun verifyURL_urlNotMatchWithReturnUrl() {
        val verifier = AuthorizingPaymentURLVerifier(
                Uri.parse("https://pay.omise.co/offsites/ofsp_test_5gfea5g4cg4trkoa4bo/pay"),
                listOf(Uri.parse("http://www.example.com"))
        )

        val wasVerified = verifier.verifyURL(Uri.parse("http://www.test.com"))

        assertFalse(wasVerified)
    }

    @Test
    fun verifyExternalURL_urlHasCustomScheme() {
        val verifier = AuthorizingPaymentURLVerifier(
                Uri.parse("https://pay.omise.co/offsites/ofsp_test_5gfea5g4cg4trkoa4bo/pay"),
                listOf(Uri.parse("app://test"))
        )

        val wasVerified = verifier.verifyExternalURL(Uri.parse("app://test"))

        assertTrue(wasVerified)
    }

    @Test
    fun verifyExternalURL_urlHasWebappScheme() {
        val verifier = AuthorizingPaymentURLVerifier(
                Uri.parse("https://pay.omise.co/offsites/ofsp_test_5gfea5g4cg4trkoa4bo/pay"),
                listOf(Uri.parse("http://www.example.com"))
        )

        val wasVerified = verifier.verifyExternalURL(Uri.parse("http://www.example.com"))

        assertFalse(wasVerified)
    }

    @Test
    fun isReady_urlWasReady() {
        val verifier = AuthorizingPaymentURLVerifier(
                Uri.parse("https://pay.omise.co/offsites/ofsp_test_5gfea5g4cg4trkoa4bo/pay"),
                listOf(Uri.parse("http://www.example.com"))
        )

        val isReady = verifier.isReady

        assertTrue(isReady)
    }
}
