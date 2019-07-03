package co.omise.android

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_AUTHORIZED_URLSTRING
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.JELLY_BEAN, Build.VERSION_CODES.P])
class AuthorizingPaymentURLVerifierTest {

    private val TEST_AUTHORIZED_URL = "https://pay.omise.co/offsites/ofsp_test_5gfea5g4cg4trkoa4bo/pay"
    private val TEST_RETURN_URL = "http://www.example.com"
    @Test
    fun createInstance_createInstanceWithIntent() {
        val intent = Intent().apply {
            putExtra(EXTRA_AUTHORIZED_URLSTRING, TEST_AUTHORIZED_URL)
            putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(TEST_RETURN_URL))
        }
        val verifier = AuthorizingPaymentURLVerifier(intent)

        assertEquals(Uri.parse(TEST_AUTHORIZED_URL), verifier.authorizedURL)
        assertEquals(listOf(Uri.parse(TEST_RETURN_URL)), verifier.expectedReturnURLPatterns)
    }

    @Test
    fun createInstance_createInstanceWithAuthorizedUrlAndExpectedUrlPatterns() {
        val verifier = AuthorizingPaymentURLVerifier(
                Uri.parse(TEST_AUTHORIZED_URL),
                listOf(Uri.parse(TEST_RETURN_URL))
        )

        assertEquals(Uri.parse(TEST_AUTHORIZED_URL), verifier.authorizedURL)
        assertEquals(listOf(Uri.parse(TEST_RETURN_URL)), verifier.expectedReturnURLPatterns)
    }

    @Test(expected = IllegalArgumentException::class)
    fun createInstance_createInstanceWithoutAuthorizedUrl() {
        val intent = Intent().apply {
            putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(TEST_RETURN_URL))
        }
        val verifier = AuthorizingPaymentURLVerifier(intent)
    }

    @Test(expected = IllegalArgumentException::class)
    fun createInstance_createInstanceWithoutReturnUrls() {
        val intent = Intent().apply {
            putExtra(EXTRA_AUTHORIZED_URLSTRING, TEST_AUTHORIZED_URL)
        }
        val verifier = AuthorizingPaymentURLVerifier(intent)
    }

    @Test
    fun verifyURL_urlWasVerified() {
        val verifier = AuthorizingPaymentURLVerifier(
                Uri.parse(TEST_AUTHORIZED_URL),
                listOf(Uri.parse(TEST_RETURN_URL))
        )

        val wasVerified = verifier.verifyURL(Uri.parse(TEST_RETURN_URL))

        assertTrue(wasVerified)
    }

    @Test
    fun verifyURL_urlNotMatchWithReturnUrl() {
        val verifier = AuthorizingPaymentURLVerifier(
                Uri.parse(TEST_AUTHORIZED_URL),
                listOf(Uri.parse(TEST_RETURN_URL))
        )

        val wasVerified = verifier.verifyURL(Uri.parse("http://www.test.com"))

        assertFalse(wasVerified)
    }

    @Test
    fun verifyExternalURL_urlHasCustomScheme() {
        val verifier = AuthorizingPaymentURLVerifier(
                Uri.parse(TEST_AUTHORIZED_URL),
                listOf(Uri.parse("app://test"))
        )

        val wasVerified = verifier.verifyExternalURL(Uri.parse("app://test"))

        assertTrue(wasVerified)
    }

    @Test
    fun verifyExternalURL_urlHasWebappScheme() {
        val verifier = AuthorizingPaymentURLVerifier(
                Uri.parse(TEST_AUTHORIZED_URL),
                listOf(Uri.parse(TEST_RETURN_URL))
        )

        val wasVerified = verifier.verifyExternalURL(Uri.parse(TEST_RETURN_URL))

        assertFalse(wasVerified)
    }

    @Test
    fun isReady_urlWasReady() {
        val verifier = AuthorizingPaymentURLVerifier(
                Uri.parse(TEST_AUTHORIZED_URL),
                listOf(Uri.parse(TEST_RETURN_URL))
        )

        val isReady = verifier.isReady

        assertTrue(isReady)
    }
}
