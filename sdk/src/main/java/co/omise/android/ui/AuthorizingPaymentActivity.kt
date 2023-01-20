package co.omise.android.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
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
import co.omise.android.config.AuthorizingPaymentConfig
import co.omise.android.threeds.challenge.ProgressView
import co.omise.android.threeds.core.ThreeDSConfig
import co.omise.android.threeds.events.CompletionEvent
import co.omise.android.threeds.events.ProtocolErrorEvent
import co.omise.android.threeds.events.RuntimeErrorEvent
import co.omise.android.ui.AuthorizingPaymentResult.*
import kotlinx.android.synthetic.main.activity_authorizing_payment.*
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
    private val threeDSConfig: ThreeDSConfig by lazy { AuthorizingPaymentConfig.get().threeDSConfig.threeDSConfig }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorizing_payment)

        supportActionBar?.title = threeDSConfig.uiCustomization?.toolbarCustomization?.headerText
                ?: getString(R.string.title_authorizing_payment)

        setupWebView()
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
        viewModel.authentication.observe(this) { result ->
            progressDialog.dismiss()

            when (result) {
                AuthenticationResult.AuthenticationUnsupported -> setupWebView()
                is AuthenticationResult.AuthenticationCompleted -> finishActivityWithSuccessful(result.completionEvent)
                is AuthenticationResult.AuthenticationFailure -> finishActivityWithFailure(result.error)
            }
        }
    }

    private fun setupWebViewClient() {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                val newUrl = "bualuangmbanking://mbanking.payment?paymentIdentifier=5UJ0ZX463J61MGZJG64&paymentRequestId=A435CB70-4668-4825-82E3-F7D69EE6E063&signature=JO2yjoeaoFgABkwg4Ex3TfYr1G0sWNUHri0pxnhnX%2BGkOJJwbiB9s9wU6jBPiiGx9mYvKzXe09xbrLFjI2ixE5SRsxfDEBfcZrDjaOtLKzgjRKqdV76LFNdOzpWXJ9NqIL%2BorRkiRSKJuE5et%2Fvl7nLC68D4ejpccGYdXl%2BhoB221Ki1DMghLpbd9KBag2z5vBjTRQCAaZUxABXrhSj%2BdkPnZz8sQ1PVjdiWQcyotP4EYKm7VaTP6th2NslkSnB0e1z633mcjfXeaySmOH7uHtNDtroy1kh94wHY8RKv3223%2BhcGZocxYKpjdMgoJx0hSCQAY8Hv0urxdhpsX3ncPg%3D%3D&siteName=OMISEAPP&timeStamp=2023-01-20T10:42:35.481+07:00"
                val uri = Uri.parse(newUrl)
                return if (verifier.verifyURL(uri)) {
                    Log.i("testaa", "url verified")
                    finishActivityWithSuccessful(url)
                    true
                } else if (verifier.verifyExternalURL(uri)) {
                    try {
                        //val externalIntent = Intent(Intent.ACTION_VIEW, uri)
                        //startActivityForResult(externalIntent, REQUEST_EXTERNAL_CODE)
                        //Log.i("testaa", "app verified " + uri)
                        //true
                        false
                    } catch (e: ActivityNotFoundException) {
                        Log.i("testaa", "verify exception " + uri)
                        e.printStackTrace()
                        true
                    }
                } else {
                    Log.i("testaa", "verify false")
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
                webView.visibility = View.VISIBLE
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
         * [AuthorizingPaymentResult] intent result from [AuthorizingPaymentActivity].
         */
        const val EXTRA_AUTHORIZING_PAYMENT_RESULT = "OmiseActivity.authorizingPaymentResult"
    }
}
