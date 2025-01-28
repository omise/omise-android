package co.omise.android.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import co.omise.android.extensions.parcelable
import co.omise.android.models.Source
import co.omise.android.models.Token

/**
 * PaymentCreatorActivity is the parent activity that controls the navigation between
 * all the payment method fragments.
 */
class PaymentCreatorActivity : OmiseActivity() {
    private lateinit var pkey: String
    private var amount: Long = 0L
    private lateinit var currency: String
    private lateinit var googlepayMerchantId: String
    private var googlepayRequestBillingAddress: Boolean = false
    private var googlepayRequestPhoneNumber: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        // Prepare arguments to pass to Flutter
        val arguments = mapOf(
            "pkey" to pkey,
            "amount" to amount,
            "currency" to currency,
        )

        // Launch FlutterUIHostActivity with the desired route and arguments
        FlutterUIHostActivity.launchActivity(
            this,
            "selectPaymentMethod",   // Flutter route or function to invoke
            arguments                // Pass arguments as a map
        )

        finish()
    }

    // TODO: find a way to unit test ActivityResult launcher in order to be able to move from deprecated onActivityResult
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CREDIT_CARD_WITH_SOURCE && resultCode == Activity.RESULT_OK) {
            val token = data?.parcelable<Token>(EXTRA_TOKEN_OBJECT)
            val source = data?.parcelable<Source>(EXTRA_SOURCE_OBJECT)
            val intent =
                Intent().apply {
                    putExtra(EXTRA_TOKEN, token?.id)
                    putExtra(EXTRA_TOKEN_OBJECT, token)
                    putExtra(EXTRA_CARD_OBJECT, token?.card)
                    putExtra(EXTRA_SOURCE_OBJECT, source)
                }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        if (requestCode == REQUEST_CREDIT_CARD && resultCode == Activity.RESULT_OK) {
            val token = data?.parcelable<Token>(EXTRA_TOKEN_OBJECT)
            val intent =
                Intent().apply {
                    putExtra(EXTRA_TOKEN, token?.id)
                    putExtra(EXTRA_TOKEN_OBJECT, token)
                    putExtra(EXTRA_CARD_OBJECT, token?.card)
                }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun initialize() {
        listOf(EXTRA_PKEY, EXTRA_AMOUNT, EXTRA_CURRENCY).forEach {
            require(intent.hasExtra(it)) { "Could not found $it." }
        }
        pkey = requireNotNull(intent.getStringExtra(EXTRA_PKEY)) { "${::EXTRA_PKEY.name} must not be null." }
        amount = intent.getLongExtra(EXTRA_AMOUNT, 0)
        currency = requireNotNull(intent.getStringExtra(EXTRA_CURRENCY)) { "${::EXTRA_CURRENCY.name} must not be null." }
        googlepayMerchantId = intent.getStringExtra(EXTRA_GOOGLEPAY_MERCHANT_ID) ?: "[GOOGLEPAY_MERCHANT_ID]"
        googlepayRequestBillingAddress = intent.getBooleanExtra(EXTRA_GOOGLEPAY_REQUEST_BILLING_ADDRESS, false)
        googlepayRequestPhoneNumber = intent.getBooleanExtra(EXTRA_GOOGLEPAY_REQUEST_PHONE_NUMBER, false)
    }

    companion object {
        const val REQUEST_CREDIT_CARD = 100

        // Used for payment methods that require both token and source to be created and the
        // credit card activity is responsible for creating both source and token
        const val REQUEST_CREDIT_CARD_WITH_SOURCE = 101
    }
}
