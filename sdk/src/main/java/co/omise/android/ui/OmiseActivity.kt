package co.omise.android.ui

import android.content.Intent
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity

/**
 * OmiseActivity is the base class for all other activities in the SDK.
 */
abstract class OmiseActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_PKEY = "OmiseActivity.publicKey"
        const val EXTRA_SOURCE_OBJECT = "OmiseActivity.sourceObject"
        const val EXTRA_AMOUNT = "OmiseActivity.amount"
        const val EXTRA_CURRENCY = "OmiseActivity.currency"
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

    @VisibleForTesting
    fun performActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        onActivityResult(requestCode, resultCode, data)
    }
}
