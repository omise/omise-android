package co.omise.android.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_RETURNED_URLSTRING
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.REQUEST_EXTERNAL_CODE
import co.omise.android.R
import co.omise.android.config.AuthorizingPaymentConfig
import co.omise.android.threeds.ThreeDS
import co.omise.android.threeds.ThreeDSListener
import co.omise.android.threeds.core.ThreeDSConfig
import co.omise.android.threeds.ui.ProgressView
import kotlinx.android.synthetic.main.activity_authorizing_payment.authorizing_payment_webview

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

    private lateinit var omisePublicKey: String
    private lateinit var tokenID: String
    private lateinit var viewModel: AuthorizingPaymentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorizing_payment)

        require(intent.hasExtra(OmiseActivity.EXTRA_PKEY)) { "Can not found ${OmiseActivity.Companion::EXTRA_PKEY.name}." }
        require(intent.hasExtra(OmiseActivity.EXTRA_TOKEN)) { "Can not found ${OmiseActivity.Companion::EXTRA_TOKEN.name}." }

        omisePublicKey = requireNotNull(intent.getStringExtra(OmiseActivity.EXTRA_PKEY)) { "${OmiseActivity.Companion::EXTRA_PKEY.name} must not be null." }
        tokenID = requireNotNull(intent.getStringExtra(OmiseActivity.EXTRA_TOKEN)) { "${OmiseActivity.Companion::EXTRA_TOKEN.name} must not be null." }

        viewModel = ViewModelProvider(this, AuthorizingPaymentViewModelFactory(omisePublicKey)).get(AuthorizingPaymentViewModel::class.java)

        ThreeDSConfig.initialize(AuthorizingPaymentConfig.get().threeDSConfig.threeDSConfig)

        initializeWebView()

        supportActionBar?.setTitle(R.string.title_authorizing_payment)

        setupWebViewClient()

        progressDialog.show()
        threeDS.authorizeTransaction(verifier.authorizedURLString)

        observeData()
    }

    private fun observeData() {
        viewModel.authorizingPaymentResult.observe(this, { result ->
            if (result.isSuccess) {
                val intent = Intent().apply {
                    putExtra(OmiseActivity.EXTRA_TOKEN_OBJECT, result.getOrNull())
                }
                authorizeSuccessful(intent)
            } else {
                authorizeFailed(result.exceptionOrNull())
            }
        })
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
        runOnUiThread {
            if (verifier.isReady) {
                webView.loadUrl(verifier.authorizedURLString)
            }
        }
    }

    private fun authorizeSuccessful(data: Intent? = null) {
        progressDialog.dismiss()
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun authorizeFailed(error: Throwable? = null) {
        progressDialog.dismiss()
        val errorIntent = Intent().apply {
            putExtra(OmiseActivity.EXTRA_ERROR, error?.message)
        }
        setResult(Activity.RESULT_CANCELED, errorIntent)
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
        viewModel.cleanup()

        super.onDestroy()
    }

    override fun onBackPressed() {
        authorizeFailed()
    }

    override fun onAuthenticated() {
        viewModel.observeTokenChange(tokenID)
    }

    override fun onUnsupported() {
        loadAuthorizeUrl()
    }

    override fun onError(e: Throwable) {
        authorizeFailed(e)
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
        cookieManager.removeAllCookies(null)
        cookieManager.flush()
    }
}
