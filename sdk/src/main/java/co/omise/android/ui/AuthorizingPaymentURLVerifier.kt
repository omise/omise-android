package co.omise.android.ui

import android.content.Intent
import android.net.Uri
import co.omise.android.ui.AuthorizingPaymentActivity.EXTRA_AUTHORIZED_URLSTRING
import co.omise.android.ui.AuthorizingPaymentActivity.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS
import java.lang.IllegalArgumentException


class AuthorizingPaymentURLVerifier {
    val authorizedURL: Uri
    val expectedReturnURLPatterns: List<Uri>

    val isReady: Boolean
        get() = (authorizedURL.toString().isNotEmpty()
                && expectedReturnURLPatterns.isNotEmpty())

    val authorizedURLString: String
        get() =
            authorizedURL.toString()

    constructor(authorizedURL: Uri, expectedReturnURLPatterns: List<Uri>) {
        this.authorizedURL = authorizedURL
        this.expectedReturnURLPatterns = expectedReturnURLPatterns
    }

    constructor(intent: Intent) {
        val authorizedURLString = intent.getStringExtra(EXTRA_AUTHORIZED_URLSTRING)
        val returnURLStringPatterns = intent.getStringArrayExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS)

        if (authorizedURLString.isNullOrEmpty()) {
            throw IllegalArgumentException("Couldn't find argument: ${::EXTRA_AUTHORIZED_URLSTRING.name}.")
        }
        if (returnURLStringPatterns.isNullOrEmpty()) {
            throw IllegalArgumentException("Couldn't find argument: ${::EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS.name}.")
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