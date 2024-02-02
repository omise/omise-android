package co.omise.android.utils

import android.util.Base64
import android.view.View
import android.webkit.WebView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher

/**
 * Load html to WebView.
 */
fun loadHtml(html: String): ViewAction =
    object : ViewAction {
        override fun getDescription(): String {
            return "Load html to WebView"
        }

        override fun getConstraints(): Matcher<View> {
            return allOf(isAssignableFrom(WebView::class.java))
        }

        override fun perform(
            uiController: UiController?,
            view: View?,
        ) {
            val webView = view as? WebView ?: return
            val encodedHtml = Base64.encodeToString(html.toByteArray(), Base64.NO_PADDING)
            webView.loadData(encodedHtml, "text/html", "base64")
        }
    }

/**
 * Load url to WebView.
 */
fun loadUrl(url: String): ViewAction =
    object : ViewAction {
        override fun getDescription(): String = "Load url to WebView: $url"

        override fun getConstraints(): Matcher<View> = allOf(isAssignableFrom(WebView::class.java))

        override fun perform(
            uiController: UiController?,
            view: View?,
        ) {
            val webView = view as? WebView ?: return
            webView.let {
                uiController?.loopMainThreadUntilIdle()
                it.loadUrl(url)
            }
        }
    }
