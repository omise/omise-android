package co.omise.android

import android.content.Intent
import android.net.Uri


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

    fun verifyURL(uri: Uri): Boolean {
        for (expectedReturnURLPattern in expectedReturnURLPatterns) {
            if (expectedReturnURLPattern.scheme!!.equals(uri.scheme!!, ignoreCase = true) &&
                    expectedReturnURLPattern.host!!.equals(uri.host!!, ignoreCase = true) &&
                    uri.path!!.startsWith(expectedReturnURLPattern.path!!)) {
                return true
            }
        }

        return false
    }

    fun verifyExternalURL(uri: Uri): Boolean {
        return uri.scheme != "http" &&
                uri.scheme != "https" &&
                uri.scheme != "about"
    }
}