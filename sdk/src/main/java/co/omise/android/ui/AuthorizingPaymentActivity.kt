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
import co.omise.android.OmiseException
import co.omise.android.R
import co.omise.android.threeds.events.CompletionEvent
import co.omise.android.threeds.events.ProtocolErrorEvent
import co.omise.android.threeds.events.RuntimeErrorEvent
import co.omise.android.threeds.ui.ProgressView
import co.omise.android.ui.AuthoringPaymentResult.Failure
import co.omise.android.ui.AuthoringPaymentResult.ThreeDS1Completed
import co.omise.android.ui.AuthoringPaymentResult.ThreeDS2Completed
import kotlinx.android.synthetic.main.activity_authorizing_payment.authorizing_payment_webview
import org.jetbrains.annotations.TestOnly
import java.net.ProtocolException

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
                is AuthenticationResult.AuthenticationCompleted -> finishActivityWithSuccessful(result.completionEvent)
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

    private fun finishActivityWithSuccessful(returnedUrl: String) {
        val resultIntent = Intent().apply {
            putExtra(EXTRA_RETURNED_URLSTRING, returnedUrl)
            putExtra(EXTRA_AUTHORIZING_PAYMENT_RESULT, ThreeDS1Completed(returnedUrl))
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun finishActivityWithSuccessful(completionEvent: CompletionEvent) {
        val resultIntent = Intent().apply {
            putExtra(EXTRA_AUTHORIZING_PAYMENT_RESULT, ThreeDS2Completed(completionEvent.sdkTransactionId, completionEvent.transactionStatus.value))
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun finishActivityWithSuccessful(data: Intent?) {
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun finishActivityWithFailure(throwable: Throwable? = null) {
        val exception = when (throwable) {
            is ProtocolErrorEvent ->
                OmiseException("3D Secure authorization failed: protocol error.", ProtocolException(
                        """
                            errorCode=${throwable.errorMessage.errorCode?.value},
                            errorDetail=${throwable.errorMessage.errorDetail},
                            errorDescription=${throwable.errorMessage.errorDescription},
                        """.trimIndent()
                ))
            is RuntimeErrorEvent ->
                OmiseException("3D Secure authorization failed: runtime error.", RuntimeException(throwable.errorMessage))
            else ->
                OmiseException("3D Secure authorization failed: ${throwable?.message}", throwable)
        }
        val resultIntent = Intent().apply {
            putExtra(EXTRA_AUTHORIZING_PAYMENT_RESULT, Failure(exception))
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    companion object {
        /**
         * [AuthoringPaymentResult] intent result from [AuthorizingPaymentActivity].
         */
        const val EXTRA_AUTHORIZING_PAYMENT_RESULT = "OmiseActivity.authorizingPaymentResult"
    }
}
