package co.omise.android.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.ViewModelProvider
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_RETURNED_URLSTRING
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.REQUEST_EXTERNAL_CODE
import co.omise.android.R
import co.omise.android.threeds.data.models.TransactionStatus
import co.omise.android.threeds.events.ProtocolErrorEvent
import co.omise.android.threeds.events.RuntimeErrorEvent
import co.omise.android.threeds.ui.ProgressView
import kotlinx.android.synthetic.main.activity_authorizing_payment.authorizing_payment_webview
import org.jetbrains.annotations.TestOnly

/**
 * AuthorizingPaymentActivity is an experimental helper UI class in the SDK that would help
 * the implementer with handling 3DS verification process within their app out of the box.
 * In case the authorization needs to be handled by an external app, the SDK opens that external
 * app by default but the Intent callback needs to be handled by the implementer.
 */
class AuthorizingPaymentActivity : AppCompatActivity() {

    private val progressDialog: ProgressView by lazy { ProgressView.newInstance(this) }
    private val webView: WebView by lazy { authorizing_payment_webview }
    private val verifier: AuthorizingPaymentURLVerifier by lazy { AuthorizingPaymentURLVerifier(intent) }

    private lateinit var viewModel: AuthorizingPaymentViewModel
    private var viewModelFactory: ViewModelProvider.Factory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorizing_payment)

        supportActionBar?.setTitle(R.string.title_authorizing_payment)

        viewModel = ViewModelProvider(this, getAuthorizingPaymentViewModelFactory()).get(AuthorizingPaymentViewModel::class.java)

        progressDialog.show()

        viewModel.authorizeTransaction(verifier.authorizedURLString)

        observeData()
    }

    private fun getAuthorizingPaymentViewModelFactory(): ViewModelProvider.Factory {
        if (viewModelFactory == null) {
            viewModelFactory = AuthorizingPaymentViewModelFactory(this)
        }
        return viewModelFactory ?: throw IllegalArgumentException("viewModelFactory must not be null.")
    }

    @TestOnly
    fun setAuthorizingPaymentViewModelFactory(viewModelFactory: ViewModelProvider.Factory) {
        this.viewModelFactory = viewModelFactory
    }

    private fun observeData() {
        viewModel.authentication.observe(this, { result ->
            progressDialog.dismiss()

            when (result) {
                AuthenticationResult.AuthenticationUnsupported -> setupWebView()
                is AuthenticationResult.AuthenticationCompleted -> finishActivityWithSuccessful(result.transStatus)
                is AuthenticationResult.AuthenticationFailure -> finishActivityWithFailure(result.error)
            }
        })
    }

    private fun setupWebViewClient() {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                val uri = Uri.parse(url)
                return if (verifier.verifyURL(uri)) {
                    finishActivityWithSuccessful(url)
                    true
                } else if (verifier.verifyExternalURL(uri)) {
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
                        .setPositiveButton(android.R.string.ok) { _, _ -> result?.confirm(promptEditText.text.toString()) }
                        .setNegativeButton(android.R.string.cancel) { _, _ -> result?.cancel() }
                        .setOnCancelListener { result?.cancel() }
                        .show()
                return true
            }
        }
    }

    private fun finishActivityWithSuccessful(returnedUrl: String) {
        val successfulIntent = Intent().apply { putExtra(EXTRA_RETURNED_URLSTRING, returnedUrl) }
        setResult(Activity.RESULT_OK, successfulIntent)
        finish()
    }

    private fun finishActivityWithSuccessful(transactionStatus: TransactionStatus) {
        val successfulIntent = Intent().apply { putExtra(OmiseActivity.EXTRA_TRANSACTION_STATUS, transactionStatus.value) }
        setResult(Activity.RESULT_OK, successfulIntent)
        finish()
    }

    private fun finishActivityWithSuccessful(data: Intent?) {
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun finishActivityWithFailure(error: Throwable? = null) {
        val errorMessage = when (error) {
            is ProtocolErrorEvent -> error.errorMessage.errorDetail
            is RuntimeErrorEvent -> error.errorMessage
            else -> error?.message
        }
        val errorIntent = Intent().apply {
            putExtra(OmiseActivity.EXTRA_ERROR, "3D Secure authentication failed: $errorMessage")
        }
        setResult(Activity.RESULT_CANCELED, errorIntent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EXTERNAL_CODE && resultCode == RESULT_OK) {
            finishActivityWithSuccessful(data)
        }
    }

    override fun onDestroy() {
        // Cleanup WebView
        webView.clearCache(true)
        webView.clearHistory()
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies(null)
        cookieManager.flush()

        // Cleanup ViewModel
        viewModel.cleanup()

        super.onDestroy()
    }

    override fun onBackPressed() {
        finishActivityWithFailure()
    }

    private fun setupWebView() {
        setupWebViewClient()
        with(webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
        }

        runOnUiThread {
            if (verifier.isReady) {
                webView.loadUrl(verifier.authorizedURLString)
            }
        }
    }
}
