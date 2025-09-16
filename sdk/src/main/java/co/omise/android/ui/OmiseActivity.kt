package co.omise.android.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

/**
 * OmiseActivity is the base class for all other activities in the SDK.
 */
abstract class OmiseActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_PKEY = "OmiseActivity.publicKey"
        const val EXTRA_SOURCE_OBJECT = "OmiseActivity.sourceObject"
        const val EXTRA_AMOUNT = "OmiseActivity.amount"
        const val EXTRA_CURRENCY = "OmiseActivity.currency"
        const val EXTRA_SELECTED_INSTALLMENTS_TERM = "OmiseActivity.selectedInstallmentsTerm"
        const val EXTRA_SELECTED_INSTALLMENTS_PAYMENT_METHOD = "OmiseActivity.selectedInstallmentsPaymentMethod"
        const val EXTRA_CAPABILITY = "OmiseActivity.capability"
        const val EXTRA_CARD_BRANDS = "OmiseActivity.cardBrands"
        const val EXTRA_GOOGLEPAY_MERCHANT_ID = "OmiseActivity.googlepayMerchantId"
        const val EXTRA_GOOGLEPAY_REQUEST_BILLING_ADDRESS = "OmiseActivity.googlepayRequestBillingAddress"
        const val EXTRA_GOOGLEPAY_REQUEST_PHONE_NUMBER = "OmiseActivity.googlepayRequestPhoneNumber"

        const val EXTRA_TOKEN = "OmiseActivity.token"
        const val EXTRA_TOKEN_OBJECT = "OmiseActivity.tokenObject"
        const val EXTRA_CARD_OBJECT = "OmiseActivity.cardObject"

        /**
         * Applies [android.view.WindowManager.LayoutParams.FLAG_SECURE] to the activity.
         * This will prevent the activity from being captured by screenshots and video recordings.
         */
        const val EXTRA_IS_SECURE = "OmiseActivity.isSecure"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupEdgeToEdge()
    }

    /**
     * Configures edge-to-edge display
     */
    private fun setupEdgeToEdge() {
        // Enable edge-to-edge display
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setupInsetsHandling()
    }

    /**
     * Sets up insets handling after the content view is available
     */
    private fun setupInsetsHandling() {
        val decorView = window.decorView
        decorView.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    decorView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    handleWindowInsets()
                }
            },
        )
    }

    /**
     * Handles window insets for the content view to prevent overlay issues.
     * Child activities can override this method for custom inset handling.
     */
    protected open fun handleWindowInsets() {
        val decorView = window.decorView
        val rootView = decorView.findViewById<View>(android.R.id.content)

        ViewCompat.setOnApplyWindowInsetsListener(decorView) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            if (systemBars.top > 0 || systemBars.bottom > 0 || systemBars.left > 0 || systemBars.right > 0) {
                rootView?.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom,
                )
            }
            insets
        }
    }

    @VisibleForTesting
    fun performActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        onActivityResult(requestCode, resultCode, data)
    }
}
