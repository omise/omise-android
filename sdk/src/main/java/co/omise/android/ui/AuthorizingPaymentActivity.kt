package co.omise.android.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_RETURNED_URLSTRING
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.REQUEST_EXTERNAL_CODE
import co.omise.android.R


/**
 * This is an experimental helper class in our SDK which would help you to handle 3DS verification process within your apps out of the box.
 * In case authorize with external app. By default open those external app when completed verification then sent result back our SDK.
 */
class AuthorizingPaymentActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var verifier: AuthorizingPaymentURLVerifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorizing_payment)
        webView = findViewById<View>(R.id.authorizing_payment_webview) as WebView
        webView.settings.javaScriptEnabled = true

        supportActionBar?.setTitle(R.string.title_authorizing_payment)

        verifier = AuthorizingPaymentURLVerifier(intent)
        if (verifier.isReady) {
            webView.loadUrl(verifier.authorizedURLString)
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
}

