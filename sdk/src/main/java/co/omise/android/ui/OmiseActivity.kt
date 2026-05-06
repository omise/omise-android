package co.omise.android.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
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
        const val EXTRA_CARD_HOLDER_DATA = "OmiseActivity.cardHolderData"

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

    // Override the View version for View Binding support
    override fun setContentView(view: View?) {
        super.setContentView(view)
        handleWindowInsets()
    }

    override fun setContentView(
        view: View?,
        params: android.view.ViewGroup.LayoutParams?,
    ) {
        super.setContentView(view, params)
        handleWindowInsets()
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        handleWindowInsets()
    }

    /**
     * Handles window insets for the content view to prevent overlay issues.
     * Child activities can override this method for custom inset handling.
     */
    protected open fun handleWindowInsets() {
        val rootView = findViewById<View>(android.R.id.content) ?: return

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom,
            )
            insets
        }
        // Force immediate application of insets
        ViewCompat.requestApplyInsets(rootView)
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
