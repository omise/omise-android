package co.omise.android.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import co.omise.android.extensions.parcelable
import co.omise.android.models.Source
import co.omise.android.models.Token

class GooglePayActivity : AppCompatActivity() {
    private lateinit var pKey: String
    private lateinit var cardNetworks: ArrayList<String>
    private var price: Long = 0
    private lateinit var currencyCode: String
    private lateinit var merchantId: String
    private var requestBillingAddress: Boolean = false
    private var requestPhoneNumber: Boolean = false

    private lateinit var flutterActivityLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        flutterActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val token = result.data?.parcelable<Token>(OmiseActivity.EXTRA_TOKEN_OBJECT)
            val source = result.data?.parcelable<Source>(OmiseActivity.EXTRA_SOURCE_OBJECT)
            val intent = Intent().apply {
                token?.let {
                    putExtra(OmiseActivity.EXTRA_TOKEN, it.id)
                    putExtra(OmiseActivity.EXTRA_TOKEN_OBJECT, it)
                    putExtra(OmiseActivity.EXTRA_CARD_OBJECT, it.card)
                }

                source?.let {
                    putExtra(OmiseActivity.EXTRA_SOURCE_OBJECT, it)
                }
            }
            setResult(result.resultCode, intent)
            finish()
        }
        // Prepare arguments to pass to Flutter
        val arguments = mapOf(
            "pkey" to pKey,
            "amount" to price,
            "currency" to currencyCode,
            "googlePayMerchantId" to merchantId,
            "googlePayRequestBillingAddress" to requestBillingAddress,
            "googlePayRequestPhoneNumber" to requestPhoneNumber,
        )

        // Launch FlutterUIHostActivity with the desired route and arguments
        FlutterUIHostActivity.launchActivity(
            flutterActivityLauncher,
            this,
            "openGooglePay",   // Flutter function to invoke
            arguments  // Pass arguments as a map
        )

    }

    private fun initialize() {
        pKey =
            requireNotNull(intent.getStringExtra(OmiseActivity.EXTRA_PKEY)) {
                "${OmiseActivity.Companion::EXTRA_PKEY.name} must not be null."
            }
        cardNetworks =
            requireNotNull(intent.getStringArrayListExtra(OmiseActivity.EXTRA_CARD_BRANDS)) {
                "${OmiseActivity.Companion::EXTRA_CARD_BRANDS.name} must not be null."
            }
        price =
            requireNotNull(intent.getLongExtra(OmiseActivity.EXTRA_AMOUNT, 0)) {
                "${OmiseActivity.Companion::EXTRA_AMOUNT.name} must not be null."
            }
        currencyCode =
            requireNotNull(intent.getStringExtra(OmiseActivity.EXTRA_CURRENCY)) {
                "${OmiseActivity.Companion::EXTRA_CURRENCY.name} must not be null."
            }
        merchantId =
            requireNotNull(intent.getStringExtra(OmiseActivity.EXTRA_GOOGLEPAY_MERCHANT_ID)) {
                "${OmiseActivity.Companion::EXTRA_GOOGLEPAY_MERCHANT_ID.name} must not be null."
            }
        requestBillingAddress =
            requireNotNull(intent.getBooleanExtra(OmiseActivity.EXTRA_GOOGLEPAY_REQUEST_BILLING_ADDRESS, false)) {
                "${OmiseActivity.Companion::EXTRA_GOOGLEPAY_REQUEST_BILLING_ADDRESS.name} must not be null."
            }
        requestPhoneNumber =
            requireNotNull(intent.getBooleanExtra(OmiseActivity.EXTRA_GOOGLEPAY_REQUEST_PHONE_NUMBER, false)) {
                "${OmiseActivity.Companion::EXTRA_GOOGLEPAY_REQUEST_PHONE_NUMBER.name} must not be null."
            }
    }

}