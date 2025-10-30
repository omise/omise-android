package co.omise.android.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import co.omise.android.BuildConfig
import co.omise.android.extensions.parcelable
import co.omise.android.models.Capability
import co.omise.android.models.CardHolderDataList
import co.omise.android.models.Source
import co.omise.android.models.Token
import co.omise.android.models.toFlutterString

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
    private lateinit var cardHolderDataList: CardHolderDataList
    private var customCapability: Capability? = null

    @VisibleForTesting
    lateinit var flutterActivityLauncher: ActivityResultLauncher<Intent>

    @VisibleForTesting
    fun handleFlutterResult(
        resultCode: Int,
        data: Intent?,
    ) {
        val token = data?.parcelable<Token>(EXTRA_TOKEN_OBJECT)
        val source = data?.parcelable<Source>(EXTRA_SOURCE_OBJECT)
        val intent =
            Intent().apply {
                token?.let {
                    putExtra(EXTRA_TOKEN, it.id)
                    putExtra(EXTRA_TOKEN_OBJECT, it)
                    putExtra(EXTRA_CARD_OBJECT, it.card)
                }

                source?.let {
                    putExtra(EXTRA_SOURCE_OBJECT, it)
                }
            }
        setResult(resultCode, intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        flutterActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                handleFlutterResult(result.resultCode, result.data)
            }
        // Prepare arguments to pass to Flutter
        val arguments =
            mapOf(
                "pkey" to pkey,
                "amount" to amount,
                "currency" to currency,
                "selectedPaymentMethods" to customCapability?.paymentMethods?.map { it.name },
                "selectedTokenizationMethods" to customCapability?.tokenizationMethods,
                "googlePayMerchantId" to googlepayMerchantId,
                "googlePayRequestBillingAddress" to googlepayRequestBillingAddress,
                "googlePayRequestPhoneNumber" to googlepayRequestPhoneNumber,
                // TODO: Replace hard coded data with user input once the requested data is added to the SDK
                "atomeItems" to
                    listOf(
                        mapOf(
                            "sku" to "3427842",
                            "category" to "Shoes",
                            "name" to "Prada shoes",
                            "quantity" to 1,
                            "amount" to amount,
                            "item_uri" to "www.kan.com/product/shoes",
                            "image_uri" to "www.kan.com/product/shoes/image",
                            "brand" to "Gucci",
                        ),
                    ),
                "securePaymentFlag" to intent.getBooleanExtra(EXTRA_IS_SECURE, true),
                "environment" to if (BuildConfig.FLAVOR.contains("staging")) "staging" else "production",
                "cardHolderData" to cardHolderDataList.fields.map { it.toFlutterString() },
            )

        // Launch FlutterUIHostActivity with the desired route and arguments
        FlutterUIHostActivity.launchActivity(
            flutterActivityLauncher,
            this,
            // Flutter function to invoke
            "selectPaymentMethod",
            // Pass arguments as a map
            arguments,
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
        customCapability = intent.parcelable(EXTRA_CAPABILITY)
        cardHolderDataList = intent.parcelable<CardHolderDataList>(EXTRA_CARD_HOLDER_DATA) ?: CardHolderDataList(arrayListOf())
    }

    companion object {
    }
}
