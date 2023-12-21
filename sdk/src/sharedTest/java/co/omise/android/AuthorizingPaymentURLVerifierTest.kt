package co.omise.android

import android.content.Intent
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_AUTHORIZED_URLSTRING
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthorizingPaymentURLVerifierTest {
    private val testAuthorizedUrl = "https://pay.omise.co/offsites/ofsp_test_5gfea5g4cg4trkoa4bo/pay"
    private val testReturnUrl = "http://www.example.com"

    @Test
    fun createInstance_createInstanceWithIntent() {
        val intent =
            Intent().apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, testAuthorizedUrl)
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(testReturnUrl))
            }
        val verifier = AuthorizingPaymentURLVerifier(intent)

        assertEquals(Uri.parse(testAuthorizedUrl), verifier.authorizedURL)
        assertEquals(listOf(Uri.parse(testReturnUrl)), verifier.expectedReturnURLPatterns)
    }

    @Test
    fun createInstance_createInstanceWithAuthorizedUrlAndExpectedUrlPatterns() {
        val verifier =
            AuthorizingPaymentURLVerifier(
                Uri.parse(testAuthorizedUrl),
                listOf(Uri.parse(testReturnUrl)),
            )

        assertEquals(Uri.parse(testAuthorizedUrl), verifier.authorizedURL)
        assertEquals(listOf(Uri.parse(testReturnUrl)), verifier.expectedReturnURLPatterns)
    }

    @Test(expected = IllegalArgumentException::class)
    fun createInstance_createInstanceWithoutAuthorizedUrl() {
        val intent =
            Intent().apply {
                putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(testReturnUrl))
            }
        val verifier = AuthorizingPaymentURLVerifier(intent)
    }

    @Test(expected = IllegalArgumentException::class)
    fun createInstance_createInstanceWithoutReturnUrls() {
        val intent =
            Intent().apply {
                putExtra(EXTRA_AUTHORIZED_URLSTRING, testAuthorizedUrl)
            }
        val verifier = AuthorizingPaymentURLVerifier(intent)
    }

    @Test
    fun verifyURL_urlWasVerified() {
        val verifier =
            AuthorizingPaymentURLVerifier(
                Uri.parse(testAuthorizedUrl),
                listOf(Uri.parse(testReturnUrl)),
            )

        val wasVerified = verifier.verifyURL(Uri.parse(testReturnUrl))

        assertTrue(wasVerified)
    }

    @Test
    fun verifyURL_urlNotMatchWithReturnUrl() {
        val verifier =
            AuthorizingPaymentURLVerifier(
                Uri.parse(testAuthorizedUrl),
                listOf(Uri.parse(testReturnUrl)),
            )

        val wasVerified = verifier.verifyURL(Uri.parse("http://www.test.com"))

        assertFalse(wasVerified)
    }

    @Test
    fun verifyExternalURL_urlHasCustomScheme() {
        val verifier =
            AuthorizingPaymentURLVerifier(
                Uri.parse(testAuthorizedUrl),
                listOf(Uri.parse("app://test")),
            )

        val wasVerified = verifier.verifyExternalURL(Uri.parse("app://test"))

        assertTrue(wasVerified)
    }

    @Test
    fun verifyExternalURL_urlHasWebappScheme() {
        val verifier =
            AuthorizingPaymentURLVerifier(
                Uri.parse(testAuthorizedUrl),
                listOf(Uri.parse(testReturnUrl)),
            )

        val wasVerified = verifier.verifyExternalURL(Uri.parse(testReturnUrl))

        assertFalse(wasVerified)
    }

    @Test
    fun isReady_urlWasReady() {
        val verifier =
            AuthorizingPaymentURLVerifier(
                Uri.parse(testAuthorizedUrl),
                listOf(Uri.parse(testReturnUrl)),
            )

        val isReady = verifier.isReady

        assertTrue(isReady)
    }
}
