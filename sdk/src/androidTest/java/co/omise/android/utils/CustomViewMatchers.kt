package co.omise.android.utils

import android.view.View
import android.webkit.WebView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher


/**
 * Matches a given url with the url of a WebView.
 */
fun withUrl(url: String): Matcher<View> = object : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description?) {
        description?.appendText("with webview url: $url")
    }

    override fun matchesSafely(item: View?): Boolean {
        val webView = item as? WebView ?: return false
        return webView.url == url
    }
}
