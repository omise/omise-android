package co.omise.android.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_RETURNED_URLSTRING
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.REQUEST_EXTERNAL_CODE
import co.omise.android.R
import co.omise.android.threeds.ThreeDS
import co.omise.android.threeds.ThreeDSListener
import co.omise.android.threeds.ui.ProgressView
import kotlinx.android.synthetic.main.activity_authorizing_payment.*

/**
 * AuthorizingPaymentActivity is an experimental helper UI class in the SDK that would help
 * the implementer with handling 3DS verification process within their app out of the box.
 * In case the authorization needs to be handled by an external app, the SDK opens that external
 * app by default but the Intent callback needs to be handled by the implementer.
 */
class AuthorizingPaymentActivity : AppCompatActivity(), ThreeDSListener {

    private val progressDialog: ProgressView by lazy { ProgressView.newInstance(this) }
    private val webView: WebView by lazy { authorizing_payment_webview }
    private val verifier: AuthorizingPaymentURLVerifier by lazy { AuthorizingPaymentURLVerifier(intent) }
    private val threeDS: ThreeDS by lazy {
        ThreeDS(this).apply {
            listener = this@AuthorizingPaymentActivity
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorizing_payment)

        initializeWebView()

        supportActionBar?.setTitle(R.string.title_authorizing_payment)

        setupWebViewClient()

        progressDialog.show()
        threeDS.authorizeTransaction(verifier.authorizedURLString)
    }

    private fun setupWebViewClient() {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                val uri = Uri.parse(url)
                if (verifier.verifyURL(uri)) {
                    val resultIntent = Intent().apply {
                        putExtra(EXTRA_RETURNED_URLSTRING, url)
                    }
                    authorizeSuccessful(resultIntent)
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

    private fun loadAuthorizeUrl() {
        if (verifier.isReady) {
            webView.loadUrl(verifier.authorizedURLString)
        }
    }

    private fun authorizeSuccessful(result: Intent? = null) {
        progressDialog.dismiss()
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    private fun authorizeFailed() {
        progressDialog.dismiss()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EXTERNAL_CODE && resultCode == RESULT_OK) {
            authorizeSuccessful(data)
        }
    }

    override fun onDestroy() {
        clearCache()
        threeDS.cleanup()

        super.onDestroy()
    }

    override fun onBackPressed() {
        authorizeFailed()
    }

    override fun onAuthenticated() {
        authorizeSuccessful()
    }

    override fun onUnsupported() {
        loadAuthorizeUrl()
    }

    override fun onError(e: Throwable) {
        authorizeFailed()
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
}
