package co.omise.android.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.VisibleForTesting
import co.omise.android.BuildConfig
import co.omise.android.extensions.parcelable
import co.omise.android.models.CardHolderDataList
import co.omise.android.models.Source
import co.omise.android.models.Token
import co.omise.android.models.toFlutterString

/**
 * CreditCardActivity is the UI class for taking credit card information input from the user.
 */
class CreditCardActivity : OmiseActivity() {
    private lateinit var pKey: String
    private lateinit var cardHolderData: CardHolderDataList

    private lateinit var flutterActivityLauncher: ActivityResultLauncher<Intent>

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

        require(intent.hasExtra(EXTRA_PKEY)) { "Could not find ${::EXTRA_PKEY.name}." }
        pKey = requireNotNull(intent.getStringExtra(EXTRA_PKEY)) { "${::EXTRA_PKEY.name} must not be null." }
        cardHolderData = intent.parcelable<CardHolderDataList>(EXTRA_CARD_HOLDER_DATA) ?: CardHolderDataList(arrayListOf())
        flutterActivityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                handleFlutterResult(result.resultCode, result.data)
            }
        // Prepare arguments to pass to Flutter
        val arguments =
            mapOf(
                "pkey" to pKey,
                "securePaymentFlag" to intent.getBooleanExtra(EXTRA_IS_SECURE, true),
                // Pass the environment again since this page can be opened as a stand alone page without the need for the select payment method page
                "environment" to if (BuildConfig.FLAVOR.contains("staging")) "staging" else "production",
                "cardHolderData" to cardHolderData.fields.map { it.toFlutterString() }
            )

        // Launch FlutterUIHostActivity with the desired route and arguments
        FlutterUIHostActivity.launchActivity(
            flutterActivityLauncher,
            this,
            // Flutter function to invoke
            "openCardPage",
            // Pass arguments as a map
            arguments,
        )
    }
}
