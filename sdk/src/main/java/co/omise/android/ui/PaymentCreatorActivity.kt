package co.omise.android.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

    private lateinit var flutterActivityLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        flutterActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val token = result.data?.parcelable<Token>(EXTRA_TOKEN_OBJECT)
            val source = result.data?.parcelable<Source>(EXTRA_SOURCE_OBJECT)
            val intent = Intent().apply {
                token?.let {
                    putExtra(EXTRA_TOKEN, it.id)
                    putExtra(EXTRA_TOKEN_OBJECT, it)
                    putExtra(EXTRA_CARD_OBJECT, it.card)
                }

                source?.let {
                    putExtra(EXTRA_SOURCE_OBJECT, it)
                }
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        // Prepare arguments to pass to Flutter
        val arguments = mapOf(
            "pkey" to pkey,
            "amount" to amount,
            "currency" to currency,
        )

        // Launch FlutterUIHostActivity with the desired route and arguments
        FlutterUIHostActivity.launchActivity(
            flutterActivityLauncher,
            this,
            "selectPaymentMethod",   // Flutter function to invoke
            arguments  // Pass arguments as a map
        )
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
