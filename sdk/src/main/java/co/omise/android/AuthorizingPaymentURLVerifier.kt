package co.omise.android

import android.content.Intent
import android.net.Uri

/**
 * AuthorizingPaymentURLVerifier is a utility class that verifies the correctness and integrity
 * of an authorizing URL that is returned as part of creating a Charge through the Omise API.
 */
class AuthorizingPaymentURLVerifier {
    val authorizedURL: Uri
    val expectedReturnURLPatterns: List<Uri>

    val isReady: Boolean
        get() = (authorizedURL.toString().isNotEmpty()
                && expectedReturnURLPatterns.isNotEmpty())

    val authorizedURLString: String
        get() =
            authorizedURL.toString()

    companion object {
        const val EXTRA_AUTHORIZED_URLSTRING = "AuthorizingPaymentURLVerifier.authorizedURL"
        const val EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS = "AuthorizingPaymentURLVerifier.expectedReturnURLPatterns"
        const val EXTRA_RETURNED_URLSTRING = "AuthorizingPaymentURLVerifier.returnedURL"
        const val REQUEST_EXTERNAL_CODE = 300
    }

    constructor(authorizedURL: Uri, expectedReturnURLPatterns: List<Uri>) {
        this.authorizedURL = authorizedURL
        this.expectedReturnURLPatterns = expectedReturnURLPatterns
    }

    constructor(intent: Intent) {
        val authorizedURLString = intent.getStringExtra(EXTRA_AUTHORIZED_URLSTRING)
        val returnURLStringPatterns = intent.getStringArrayExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS)

        if (authorizedURLString.isNullOrEmpty()) {
            throw IllegalArgumentException("Couldn't find argument: ${Companion::EXTRA_AUTHORIZED_URLSTRING.name}.")
        }
        if (returnURLStringPatterns.isNullOrEmpty()) {
            throw IllegalArgumentException("Couldn't find argument: ${Companion::EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS.name}.")
        }
        authorizedURL = Uri.parse(authorizedURLString)
        expectedReturnURLPatterns = returnURLStringPatterns.map { Uri.parse(it) }
    }

    /**
     * verifyURL checks a supplied URL to see if it matches the expected scheme, host and
     * path.
     *
     * @param uri URL that needs to be verified.
     * @return true if the URL is valid, false otherwise
     */
    fun verifyURL(uri: Uri): Boolean {
        val path = uri.path ?: return false
        for (expectedReturnURLPattern in expectedReturnURLPatterns) {
            val expectedReturnPath = expectedReturnURLPattern.path ?: continue
            if (expectedReturnURLPattern.scheme.equals(uri.scheme, true) &&
                    expectedReturnURLPattern.host.equals(uri.host, true) &&
                    path.startsWith(expectedReturnPath)) {
                return true
            }
        }

        return false
    }

    /**
     * verifyExternalURL checks if a URL supplied from an external source matches
     * the expected scheme, host and path.
     *
     * @param uri URL that needs to be verified.
     * @return true if the URL is valid, false otherwise
     */
    fun verifyExternalURL(uri: Uri): Boolean {
        return uri.scheme != "http" &&
                uri.scheme != "https" &&
                uri.scheme != "about"
    }
}
