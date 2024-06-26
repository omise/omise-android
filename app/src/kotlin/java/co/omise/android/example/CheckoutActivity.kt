package co.omise.android.example

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_AUTHORIZED_URLSTRING
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS
import co.omise.android.api.Client
import co.omise.android.api.RequestListener
import co.omise.android.config.ButtonCustomization
import co.omise.android.config.ButtonCustomizationBuilder
import co.omise.android.config.ButtonType
import co.omise.android.config.LabelCustomizationBuilder
import co.omise.android.config.TextBoxCustomizationBuilder
import co.omise.android.config.ThemeConfig
import co.omise.android.config.ToolbarCustomizationBuilder
import co.omise.android.config.UiCustomizationBuilder
import co.omise.android.models.Amount
import co.omise.android.models.Source
import co.omise.android.models.Token
import co.omise.android.ui.AuthorizingPaymentActivity
import co.omise.android.ui.AuthorizingPaymentActivity.Companion.EXTRA_THREE_DS_REQUESTOR_APP_URL
import co.omise.android.ui.AuthorizingPaymentActivity.Companion.EXTRA_UI_CUSTOMIZATION
import co.omise.android.ui.AuthorizingPaymentResult
import co.omise.android.ui.CreditCardActivity
import co.omise.android.ui.OmiseActivity
import co.omise.android.ui.PaymentCreatorActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_checkout.amount_edit
import kotlinx.android.synthetic.main.activity_checkout.authorize_url_button
import kotlinx.android.synthetic.main.activity_checkout.choose_payment_method_button
import kotlinx.android.synthetic.main.activity_checkout.credit_card_button
import kotlinx.android.synthetic.main.activity_checkout.currency_edit

inline fun <reified T : Parcelable> Intent.parcelable(key: String?): T? = when {
    // https://stackoverflow.com/questions/72571804/getserializableextra-and-getparcelableextra-are-deprecated-what-is-the-alternat/73543350#73543350
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

class CheckoutActivity : AppCompatActivity() {

    companion object {

        private const val TAG = "CheckoutActivity"
        private const val PUBLIC_KEY = "[PUBLIC_KEY]"
        private const val GOOGLEPAY_MERCHANT_ID = "[GOOGLEPAY_MERCHANT_ID]"
        private const val GOOGLEPAY_REQUEST_BILLING_ADDRESS = false
        private const val GOOGLEPAY_REQUEST_PHONE_NUMBER = false


        private const val AUTHORIZING_PAYMENT_REQUEST_CODE = 0x3D5
        private const val PAYMENT_CREATOR_REQUEST_CODE = 0x3D6
        private const val CREDIT_CARD_REQUEST_CODE = 0x3D7
    }

    private val amountEdit: EditText by lazy { amount_edit }
    private val currencyEdit: EditText by lazy { currency_edit }
    private val choosePaymentMethodButton: Button by lazy { choose_payment_method_button }
    private val creditCardButton: Button by lazy { credit_card_button }
    private val authorizeUrlButton: Button by lazy { authorize_url_button }
    private val snackbar: Snackbar by lazy {
        Snackbar.make(findViewById(R.id.content), "", Snackbar.LENGTH_SHORT)
    }

    private lateinit var authorizingPaymentLauncher: ActivityResultLauncher<Intent>
    private lateinit var paymentCreatorLauncher: ActivityResultLauncher<Intent>
    private lateinit var creditCardLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        supportActionBar?.title = getString(R.string.activity_checkout)
        authorizingPaymentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            handleActivityResult(
                AUTHORIZING_PAYMENT_REQUEST_CODE,
                result.resultCode,
                result.data
            )
        }

        paymentCreatorLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            handleActivityResult(
                PAYMENT_CREATOR_REQUEST_CODE,
                result.resultCode,
                result.data
            )
        }

        creditCardLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            handleActivityResult(
                CREDIT_CARD_REQUEST_CODE,
                result.resultCode,
                result.data
            )
        }


        choosePaymentMethodButton.setOnClickListener { choosePaymentMethod() }
        creditCardButton.setOnClickListener { payByCreditCard() }
        authorizeUrlButton.setOnClickListener {
            AuthorizingPaymentDialog.showAuthorizingPaymentDialog(this) { authorizeUrl, returnUrl ->
                startAuthoringPaymentActivity(authorizeUrl, returnUrl)
            }
        }

    }

    private fun choosePaymentMethod() {
        val isUsedSpecificsPaymentMethods = PaymentSetting.isUsedSpecificsPaymentMethods(this)

        val localAmount = amountEdit.text.toString().trim().toDouble()
        val currency = currencyEdit.text.toString().trim().lowercase()
        val amount = Amount.fromLocalAmount(localAmount, currency)

        Intent(this, PaymentCreatorActivity::class.java).run {
            putExtra(OmiseActivity.EXTRA_PKEY, PUBLIC_KEY)
            putExtra(OmiseActivity.EXTRA_AMOUNT, amount.amount)
            putExtra(OmiseActivity.EXTRA_CURRENCY, amount.currency)
            putExtra(OmiseActivity.EXTRA_GOOGLEPAY_MERCHANT_ID, GOOGLEPAY_MERCHANT_ID)
            putExtra(OmiseActivity.EXTRA_GOOGLEPAY_REQUEST_BILLING_ADDRESS, GOOGLEPAY_REQUEST_BILLING_ADDRESS)
            putExtra(OmiseActivity.EXTRA_GOOGLEPAY_REQUEST_PHONE_NUMBER, GOOGLEPAY_REQUEST_PHONE_NUMBER)

            if (isUsedSpecificsPaymentMethods) {
                putExtra(OmiseActivity.EXTRA_CAPABILITY, PaymentSetting.createCapabilityFromPreferences(this@CheckoutActivity))
            }

            paymentCreatorLauncher.launch(this)
        }
    }


    private fun payByCreditCard() {
        Intent(this, CreditCardActivity::class.java).run {
            putExtra(OmiseActivity.EXTRA_PKEY, PUBLIC_KEY)
            creditCardLauncher.launch(this)
        }
    }

    private fun startAuthoringPaymentActivity(authorizeUrl: String, returnUrl: String) {
        val labelCustomization = LabelCustomizationBuilder()
            .headingTextColor("#000000")
            .headingTextFontName("roboto_mono")
            .headingTextFontSize(20)
            .textFontName("roboto_mono")
            .textColor("#000000")
            .textFontSize(16)
            .build()

        val textBoxCustomization = TextBoxCustomizationBuilder()
            .textFontName("font/roboto_mono_regular.ttf")
            .textColor("#000000")
            .textFontSize(16)
            .borderWidth(1)
            .cornerRadius(4)
            .borderColor("#1A56F0")
            .build()

        val toolbarCustomization = ToolbarCustomizationBuilder()
            .textFontName("font/roboto_mono_bold.ttf")
            .textColor("#000000")
            .textFontSize(20)
            .backgroundColor("#FFFFFF")
            .headerText("Secure Checkout")
            .buttonText("Close")
            .build()

        val primaryButtonCustomization = ButtonCustomizationBuilder()
            .textFontName("font/roboto_mono_bold.ttf")
            .textFontSize(20)
            .cornerRadius(4)
            .textColor("#FFFFFF")
            .backgroundColor("#1A56F0")
            .build()

        val secondaryButtonCustomization = ButtonCustomizationBuilder()
            .textFontName("font/roboto_mono_bold.ttf")
            .textFontSize(20)
            .cornerRadius(4)
            .textColor("#1A56F0")
            .backgroundColor("#FFFFFF")
            .build()
        val buttonCustomizations: MutableMap<ButtonType, ButtonCustomization> = mutableMapOf()
        buttonCustomizations[ButtonType.SUBMIT] = primaryButtonCustomization
        buttonCustomizations[ButtonType.CONTINUE] = primaryButtonCustomization
        buttonCustomizations[ButtonType.NEXT] = primaryButtonCustomization
        buttonCustomizations[ButtonType.OPEN_OOB_APP] = primaryButtonCustomization
        buttonCustomizations[ButtonType.ADD_CH] = primaryButtonCustomization
        buttonCustomizations[ButtonType.RESEND] = secondaryButtonCustomization
        buttonCustomizations[ButtonType.CANCEL] = secondaryButtonCustomization


        val uiCustomization = UiCustomizationBuilder()
            .setDefaultTheme(ThemeConfig(
                labelCustomization,
                toolbarCustomization,
                textBoxCustomization,
                buttonCustomizations
            ))
            .setDarkTheme(ThemeConfig(
                buttonCustomizations =   buttonCustomizations
            ))
            .setMonoChromeTheme(ThemeConfig())
            .build()

        Log.d(
            TAG, """
            authorizeUrl=$authorizeUrl
            returnUrl=$returnUrl
        """.trimIndent()
        )
        Intent(this, AuthorizingPaymentActivity::class.java).run {
            putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeUrl)
            putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            putExtra(EXTRA_UI_CUSTOMIZATION, uiCustomization)
            putExtra(
                EXTRA_THREE_DS_REQUESTOR_APP_URL,
                "sampleapp://omise.co/authorize_return"
            )
            authorizingPaymentLauncher.launch(this)
        }
    }

    private fun openPaymentSetting() {
        Intent(this, PaymentSettingActivity::class.java).run {
            startActivity(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_setup) {
            openPaymentSetting()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // custom result code when web view is closed
        if (resultCode == AuthorizingPaymentActivity.WEBVIEW_CLOSED_RESULT_CODE) {
            snackbar.setText(R.string.webview_closed).show()
            return
        }

        if (resultCode == RESULT_CANCELED) {
            snackbar.setText(R.string.payment_cancelled).show()
            return
        }

        if (data == null) {
            snackbar.setText(R.string.payment_success_but_no_result).show()
            return
        }

        when (requestCode) {
            AUTHORIZING_PAYMENT_REQUEST_CODE -> {
                with(data.parcelable<AuthorizingPaymentResult>(AuthorizingPaymentActivity.EXTRA_AUTHORIZING_PAYMENT_RESULT)) {
                    Log.d(TAG, this.toString())
                    val resultMessage = when (this) {
                        is AuthorizingPaymentResult.ThreeDS1Completed -> "Authorization with 3D Secure version 1 completed: returnedUrl=${returnedUrl}"
                        is AuthorizingPaymentResult.ThreeDS2Completed -> "Authorization with 3D Secure version 2 completed: transStatus=${transStatus}"
                        is AuthorizingPaymentResult.Failure -> {
                            Log.e(TAG, throwable.message, throwable.cause)
                            throwable.message ?: "Unknown error."
                        }

                        null -> "Not found the authorization result."
                    }
                    Log.d(TAG, resultMessage)
                    snackbar.setText(resultMessage).show()
                }
            }

            PAYMENT_CREATOR_REQUEST_CODE -> {
                // if the payment method requires both source and token then you will receive both objects
                // otherwise one object will be received
                if(data.hasExtra(OmiseActivity.EXTRA_TOKEN) && data.hasExtra(OmiseActivity.EXTRA_SOURCE_OBJECT)){
                    val source = data.parcelable<Source>(OmiseActivity.EXTRA_SOURCE_OBJECT)
                    val token = data.parcelable<Token>(OmiseActivity.EXTRA_TOKEN_OBJECT)
                    snackbar.setText((source?.id ?: "No source object.") + "/" + (token?.id ?: "No token object.")).show()
                    Log.d(TAG, "source: ${source?.id}")
                    Log.d(TAG, "token: ${token?.id}")
                } else if (data.hasExtra(OmiseActivity.EXTRA_SOURCE_OBJECT)) {
                    val source = data.parcelable<Source>(OmiseActivity.EXTRA_SOURCE_OBJECT)
                    snackbar.setText(source?.id ?: "No source object.").show()
                    Log.d(TAG, "source: ${source?.id}")
                } else if (data.hasExtra(OmiseActivity.EXTRA_TOKEN)) {
                    val token = data.parcelable<Token>(OmiseActivity.EXTRA_TOKEN_OBJECT)
                    snackbar.setText(token?.id ?: "No token object.").show()
                    Log.d(TAG, "token: ${token?.id}")
                }
            }

            CREDIT_CARD_REQUEST_CODE -> {
                val token = data.parcelable<Token>(OmiseActivity.EXTRA_TOKEN_OBJECT)
                snackbar.setText(token?.id ?: "No token object.").show()
                Log.d(TAG, "token: ${token?.id}")
            }

        }
    }
}
