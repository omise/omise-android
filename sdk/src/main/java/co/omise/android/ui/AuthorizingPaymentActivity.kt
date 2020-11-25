package co.omise.android.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_RETURNED_URLSTRING
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.REQUEST_EXTERNAL_CODE
import co.omise.android.R
import kotlinx.android.synthetic.main.activity_authorizing_payment.authorizing_payment_webview
import org.jetbrains.annotations.TestOnly

/**
 * AuthorizingPaymentActivity is an experimental helper UI class in the SDK that would help
 * the implementer with handling 3DS verification process within their app out of the box.
 * In case the authorization needs to be handled by an external app, the SDK opens that external
 * app by default but the Intent callback needs to be handled by the implementer.
 */
class AuthorizingPaymentActivity : AppCompatActivity() {

    private val webView: WebView by lazy { authorizing_payment_webview }
    private lateinit var verifier: AuthorizingPaymentURLVerifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorizing_payment)

        initializeWebView()

        supportActionBar?.setTitle(R.string.title_authorizing_payment)

        verifier = AuthorizingPaymentURLVerifier(intent)
        if (verifier.isReady) {
            webView.loadUrl(verifier.authorizedURLString)
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                AlertDialog.Builder(this@AuthorizingPaymentActivity)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok) { _, _ -> result?.confirm() }
                        .setOnCancelListener { result?.cancel() }
                        .show()
                return true
            }

            override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                AlertDialog.Builder(this@AuthorizingPaymentActivity)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok) { _, _ -> result?.confirm() }
                        .setNegativeButton(android.R.string.cancel) { _, _ -> result?.confirm() }
                        .setOnCancelListener { result?.cancel() }
                        .show()
                return true
            }

            override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
                val promptLayout = LinearLayout(this@AuthorizingPaymentActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    orientation = LinearLayout.VERTICAL
                }

                val promptEditText = AppCompatEditText(this@AuthorizingPaymentActivity).apply {
                    val margin = resources.getDimension(R.dimen.large_margin).toInt()
                    layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(margin, 0, margin, 0)
                    }
                    defaultValue?.let(this::setText)
                }
                promptLayout.addView(promptEditText)

                AlertDialog.Builder(this@AuthorizingPaymentActivity)
                        .setView(promptLayout)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok) { _, _ -> result?.confirm() }
                        .setNegativeButton(android.R.string.cancel) { _, _ -> result?.confirm() }
                        .setOnCancelListener { result?.cancel() }
                        .show()
                return true
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                val uri = Uri.parse(url)
                if (verifier.verifyURL(uri)) {
                    val resultIntent = Intent()
                    resultIntent.putExtra(EXTRA_RETURNED_URLSTRING, url)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                    return true
                } else return if (verifier.verifyExternalURL(uri)) {
                    try {
                        val externalIntent = Intent(Intent.ACTION_VIEW, uri)
                        startActivityForResult(externalIntent, REQUEST_EXTERNAL_CODE)
                        true
                    } catch (e: ActivityNotFoundException) {
                        e.printStackTrace()
                        false
                    }
                } else {
                    false
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EXTERNAL_CODE && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data)
            finish()
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        super.onBackPressed()
    }

    override fun onDestroy() {
        clearCache()
        super.onDestroy()
    }

    private fun initializeWebView() {
        with(webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
        }
    }

    private fun clearCache() {
        webView.clearCache(true)
        webView.clearHistory()

        val cookieManager = CookieManager.getInstance()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            cookieManager.removeAllCookies(null)
            cookieManager.flush()
        } else {
            val cookieSyncManager = CookieSyncManager.createInstance(this)
            cookieManager.removeAllCookie()
            cookieManager.removeSessionCookie()
            cookieSyncManager.startSync()
            cookieSyncManager.stopSync()
            cookieSyncManager.sync()
        }
    }

    @TestOnly
    fun loadData(htmlData: String) {
        val encodedHtml = Base64.encodeToString(htmlData.toByteArray(), Base64.NO_PADDING)
        webView.loadData(encodedHtml, "text/html", "base64")
    }
}
