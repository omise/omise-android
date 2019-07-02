package co.omise.android.ui

import android.content.Intent
import android.net.Uri
import co.omise.android.ui.AuthorizingPaymentActivity.EXTRA_AUTHORIZED_URLSTRING
import co.omise.android.ui.AuthorizingPaymentActivity.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS


class AuthorizingPaymentURLVerifier {
    var authorizedURL: Uri? = null
    var expectedReturnURLPatterns: List<Uri>? = null

    val isReady: Boolean
        get() = (authorizedURL != null
                && expectedReturnURLPatterns != null
                && expectedReturnURLPatterns!!.size > 0)

    val authorizedURLString: String?
        get() =
            authorizedURL?.toString()

    constructor(authorizedURL: Uri, expectedReturnURLPatterns: List<Uri>) {
        this.authorizedURL = authorizedURL
        this.expectedReturnURLPatterns = expectedReturnURLPatterns
    }

    constructor(intent: Intent) {
        authorizedURL = Uri.parse(intent.getStringExtra(EXTRA_AUTHORIZED_URLSTRING))
        val returnURLStringPatterns = intent.getStringArrayExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS)
        val returnURLPatternList = ArrayList<Uri>(returnURLStringPatterns.size)
        for (returnURLStringPattern in returnURLStringPatterns) {
            returnURLPatternList.add(Uri.parse(returnURLStringPattern))
        }

        expectedReturnURLPatterns = returnURLPatternList
    }

    fun verifyURL(uri: Uri): Boolean {
        for (expectedReturnURLPattern in expectedReturnURLPatterns!!) {
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