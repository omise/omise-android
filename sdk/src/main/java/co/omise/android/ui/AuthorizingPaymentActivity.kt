package co.omise.android.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.ViewModelProvider
import co.omise.android.AuthorizingPaymentURLVerifier
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_RETURNED_URLSTRING
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.REQUEST_EXTERNAL_CODE
import co.omise.android.OmiseException
import co.omise.android.R
import co.omise.android.config.UiCustomization
import co.omise.android.models.Authentication
import co.omise.android.ui.AuthorizingPaymentResult.Failure
import co.omise.android.ui.AuthorizingPaymentResult.ThreeDS1Completed
import co.omise.android.ui.AuthorizingPaymentResult.ThreeDS2Completed
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
    private val verifier: AuthorizingPaymentURLVerifier by lazy { AuthorizingPaymentURLVerifier(intent) }
    private val uiCustomization: UiCustomization by lazy { intent.getParcelableExtra(EXTRA_UI_CUSTOMIZATION) ?: UiCustomization.default }
    private lateinit var threeDSRequestorAppURL: String
    private var isWebViewSetup = false

    private val viewModel: AuthorizingPaymentViewModel by viewModels {
        viewModelFactory ?: AuthorizingPaymentViewModelFactory(
            activity = this,
            urlVerifier = verifier,
            uiCustomization = uiCustomization,
            passedThreeDSRequestorAppURL = threeDSRequestorAppURL
        )
    }
    private var viewModelFactory: ViewModelProvider.Factory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        threeDSRequestorAppURL = intent.getStringExtra(EXTRA_THREE_DS_REQUESTOR_APP_URL)
            ?: run {
                finishActivityWithFailure(OmiseException("The threeDSRequestorAppURL must be provided in the intent."))
                return
            }

        if (intent.getBooleanExtra(OmiseActivity.EXTRA_IS_SECURE, true)) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }

        setContentView(R.layout.activity_authorizing_payment)
        setupActionBarTitle()
        handlePaymentAuthorization()
    }

    @TestOnly
    fun setViewModelFactory(viewModelFactory: ViewModelProvider.Factory) {
        this.viewModelFactory = viewModelFactory
    }

    private fun observeData() {
        viewModel.authenticationStatus.observe(this) { result ->
            when (result) {
                Authentication.AuthenticationStatus.SUCCESS -> finishActivityWithSuccessful(TransactionStatus.AUTHENTICATED)
                Authentication.AuthenticationStatus.CHALLENGE_V1 -> setupWebView()
                Authentication.AuthenticationStatus.CHALLENGE -> viewModel.doChallenge(this)
                Authentication.AuthenticationStatus.FAILED -> finishActivityWithFailure(OmiseException("Authentication failed."))
            }
        }

        viewModel.isLoading.observe(this) {
            // Closing transaction will also hide the progress view.
            // So, we only show the progress view.
            if (it) {
                viewModel.getTransaction().getProgressView(this).showProgress()
            }
        }

        viewModel.error.observe(this) {
            finishActivityWithFailure(it)
        }

        viewModel.transactionStatus.observe(this) {
            finishActivityWithSuccessful(it)
        }
    }

    private fun setupWebViewClient() {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                val uri = Uri.parse(url)
                return if (verifier.verifyURL(uri)) {
                    finishActivityWithSuccessful(url)
                    true
                } else if (verifier.verifyExternalURL(uri)) {
                    openDeepLink(uri)
                    true
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

            override fun onJsPrompt(
                view: WebView?,
                url: String?,
                message: String?,
                defaultValue: String?,
                result: JsPromptResult?
            ): Boolean {
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

    private fun openDeepLink(uri: Uri) {
        try {
            val externalIntent = Intent(Intent.ACTION_VIEW, uri)
            startActivityForResult(externalIntent, REQUEST_EXTERNAL_CODE)
        } catch (e: ActivityNotFoundException) {
            finishActivityWithFailure(OmiseException("Open deep-link failed.", e))
        }
    }

    private fun setupActionBarTitle() {
        supportActionBar?.title = uiCustomization.uiCustomization.toolbarCustomization?.headerText
            ?: getString(R.string.title_authorizing_payment)
    }

    private fun handlePaymentAuthorization() {
        val authUrlString = verifier.authorizedURLString
        val authUrl=verifier.authorizedURL
        // check for legacy payments that require web view
        if (authUrlString.endsWith("/pay")) {
            setupWebView()
        } else {
            // Check if the URL needs to be opened externally
            if (verifier.verifyExternalURL(authUrl)) {
                openDeepLink(authUrl)
            }
            observeData()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_EXTERNAL_CODE) {
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
        finishActivityWithSuccessful(null)
    }

    private fun setupWebView() {
        isWebViewSetup = true
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

    private fun finishActivityWithSuccessful(status: TransactionStatus) {
        val resultIntent = Intent().apply {
            putExtra(
                EXTRA_AUTHORIZING_PAYMENT_RESULT,
                ThreeDS2Completed(status)
            )
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun finishActivityWithSuccessful(data: Intent?) {
        setResult(if (isWebViewSetup) AuthorizingPaymentActivity.WEBVIEW_CLOSED_RESULT_CODE else Activity.RESULT_OK, data)
        finish()
    }

    private fun finishActivityWithFailure(throwable: Throwable) {
        val resultIntent = Intent().apply {
            putExtra(EXTRA_AUTHORIZING_PAYMENT_RESULT, Failure(throwable))
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }


    companion object {
        /**
         * [AuthorizingPaymentResult] intent result from [AuthorizingPaymentActivity].
         */
        const val EXTRA_AUTHORIZING_PAYMENT_RESULT = "OmiseActivity.authorizingPaymentResult"

        /**
         * [co.omise.android.config.UiCustomization] intent extra for [AuthorizingPaymentActivity] to configure the UI in the challenge flow.
         * This is an optional parameter. If not provided, the default UI will be used.
         */
        const val EXTRA_UI_CUSTOMIZATION = "OmiseActivity.uiCustomization"

        /**
         * A new result code that is not in the default Activity values to indicate that the web view has been closed after the authorization url has been opened using web view
         */
        const val WEBVIEW_CLOSED_RESULT_CODE = 5

        /**
         * The threeDSRequestorAppURL of the host app. This parameter will be used to allow the external app flows to redirect back to the merchant app (OOB flow)
         */
        const val EXTRA_THREE_DS_REQUESTOR_APP_URL = "OmiseActivity.threeDSRequestorAppURL"
    }
}
