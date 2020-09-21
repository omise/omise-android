package co.omise.android.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_RETURNED_URLSTRING
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.REQUEST_EXTERNAL_CODE
import co.omise.android.R
import co.omise.android.threeds.ThreeDS
import co.omise.android.threeds.ThreeDSAuthorizingTransactionListener
import kotlinx.android.synthetic.main.activity_authorizing_payment.*

/**
 * AuthorizingPaymentActivity is an experimental helper UI class in the SDK that would help
 * the implementer with handling 3DS verification process within their app out of the box.
 * In case the authorization needs to be handled by an external app, the SDK opens that external
 * app by default but the Intent callback needs to be handled by the implementer.
 */
class AuthorizingPaymentActivity : AppCompatActivity(), ThreeDSAuthorizingTransactionListener {

    private val webView: WebView by lazy { authorizing_payment_webview }
    private lateinit var verifier: AuthorizingPaymentURLVerifier
    private lateinit var threeDS: ThreeDS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorizing_payment)
        webView.settings.javaScriptEnabled = true

        supportActionBar?.setTitle(R.string.title_authorizing_payment)

        verifier = AuthorizingPaymentURLVerifier(intent)
        threeDS = ThreeDS(this).apply {
            setAuthorizingTransactionListener(this@AuthorizingPaymentActivity)
            authorizeTransaction(verifier.authorizedURLString)
        }

        if (verifier.isReady) {
            webView.loadUrl(verifier.authorizedURLString)
        }

        setupWebViewClient()
    }

    private fun setupWebViewClient() {
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

    override fun onCompleted() {
        TODO("Not yet implemented")
    }

    override fun onUnsupported() {
        TODO("Fallback to 3DS V1 redirect flow")
    }

    override fun onError(e: Throwable) {
        TODO("Not yet implemented")
    }
}

