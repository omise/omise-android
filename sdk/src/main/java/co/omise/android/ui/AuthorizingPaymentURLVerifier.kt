package co.omise.android.ui

import android.content.Intent
import android.net.Uri
import co.omise.android.ui.AuthorizingPaymentActivity.EXTRA_AUTHORIZED_URLSTRING
import co.omise.android.ui.AuthorizingPaymentActivity.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS
import java.util.ArrayList

class AuthorizingPaymentURLVerifier() {
    var authorizedURL: Uri? = null
        private set
    private var expectedReturnURLPatterns: Array<Uri>? = null

    val isReady: Boolean
        get() = (authorizedURL != null
                && getExpectedReturnURLPatterns() != null
                && getExpectedReturnURLPatterns()!!.size > 0)

    val authorizedURLString: String?
        get() = if (authorizedURL == null) {
            null
        } else authorizedURL!!.toString()

    constructor(authorizedURL: Uri, expectedReturnURLPatterns: Array<Uri>) : this() {
        this.authorizedURL = authorizedURL
        this.expectedReturnURLPatterns = expectedReturnURLPatterns
    }

    constructor(intent: Intent) : this() {
        authorizedURL = Uri.parse(intent.getStringExtra(EXTRA_AUTHORIZED_URLSTRING))
        val returnURLStringPatterns = intent.getStringArrayExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS)
        val returnURLPatternList = ArrayList<Uri>(returnURLStringPatterns!!.size)
        for (returnURLStringPattern in returnURLStringPatterns) {
            returnURLPatternList.add(Uri.parse(returnURLStringPattern))
        }

        expectedReturnURLPatterns = Array<Uri>(returnURLPatternList.size, { Uri.parse("") })
        expectedReturnURLPatterns = returnURLPatternList.toTypedArray()
    }

    fun getExpectedReturnURLPatterns(): Array<Uri>? {
        return if (expectedReturnURLPatterns == null) {
            null
        } else expectedReturnURLPatterns
    }

    internal fun verifyURL(uri: Uri): Boolean {
        for (expectedReturnURLPattern in getExpectedReturnURLPatterns()!!) {
            if (expectedReturnURLPattern.scheme!!.equals(uri.scheme!!, ignoreCase = true) &&
                    expectedReturnURLPattern.host!!.equals(uri.host!!, ignoreCase = true) &&
                    uri.path!!.startsWith(expectedReturnURLPattern.path!!)) {
                return true
            }
        }

        return false
    }

    internal fun verifyExternalURL(uri: Uri): Boolean {
        return uri.scheme != "http" &&
                uri.scheme != "https" &&
                uri.scheme != "about"
    }
}