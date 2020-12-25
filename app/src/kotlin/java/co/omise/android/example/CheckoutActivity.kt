package co.omise.android.example

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_AUTHORIZED_URLSTRING
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS
import co.omise.android.api.Client
import co.omise.android.api.RequestListener
import co.omise.android.config.AuthorizingPaymentConfig
import co.omise.android.config.ThreeDSConfig
import co.omise.android.config.UiCustomization
import co.omise.android.models.Amount
import co.omise.android.models.Capability
import co.omise.android.models.Source
import co.omise.android.models.Token
import co.omise.android.ui.AuthoringPaymentResult
import co.omise.android.ui.AuthorizingPaymentActivity
import co.omise.android.ui.CreditCardActivity
import co.omise.android.ui.OmiseActivity
import co.omise.android.ui.PaymentCreatorActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_checkout.amount_edit
import kotlinx.android.synthetic.main.activity_checkout.authorize_url_button
import kotlinx.android.synthetic.main.activity_checkout.choose_payment_method_button
import kotlinx.android.synthetic.main.activity_checkout.credit_card_button
import kotlinx.android.synthetic.main.activity_checkout.currency_edit

class CheckoutActivity : AppCompatActivity() {

    companion object {

        private const val TAG = "CheckoutActivity"
        private const val PUBLIC_KEY = "[PUBLIC_KEY]"

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

    private var capability: Capability? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        initializeAuthoringPaymentConfig()

        supportActionBar?.title = getString(R.string.activity_checkout)

        choosePaymentMethodButton.setOnClickListener { choosePaymentMethod() }
        creditCardButton.setOnClickListener { payByCreditCard() }
        authorizeUrlButton.setOnClickListener { showAuthorizingPaymentDialog() }


        val client = Client(PUBLIC_KEY)
        val request = Capability.GetCapabilitiesRequestBuilder().build()
        client.send(request, object : RequestListener<Capability> {
            override fun onRequestSucceed(model: Capability) {
                capability = model
            }

            override fun onRequestFailed(throwable: Throwable) {
                snackbar.setText(throwable.message?.capitalize().orEmpty()).show()
            }
        })
    }

    private fun choosePaymentMethod() {
        val isUsedSpecificsPaymentMethods = PaymentSetting.isUsedSpecificsPaymentMethods(this)

        if (!isUsedSpecificsPaymentMethods && capability == null) {
            snackbar.setText(getString(R.string.error_capability_have_not_set_yet))
            return
        }

        val localAmount = amountEdit.text.toString().trim().toDouble()
        val currency = currencyEdit.text.toString().trim().toLowerCase()
        val amount = Amount.fromLocalAmount(localAmount, currency)

        Intent(this, PaymentCreatorActivity::class.java).run {
            putExtra(OmiseActivity.EXTRA_PKEY, PUBLIC_KEY)
            putExtra(OmiseActivity.EXTRA_AMOUNT, amount.amount)
            putExtra(OmiseActivity.EXTRA_CURRENCY, amount.currency)

            if (isUsedSpecificsPaymentMethods) {
                putExtra(OmiseActivity.EXTRA_CAPABILITY, PaymentSetting.createCapabilityFromPreferences(this@CheckoutActivity))
            } else {
                putExtra(OmiseActivity.EXTRA_CAPABILITY, capability)
            }

            startActivityForResult(this, PAYMENT_CREATOR_REQUEST_CODE)
        }
    }

    private fun payByCreditCard() {
        Intent(this, CreditCardActivity::class.java).run {
            putExtra(OmiseActivity.EXTRA_PKEY, PUBLIC_KEY)
            startActivityForResult(this, CREDIT_CARD_REQUEST_CODE)
        }
    }

    /**
     * Here's the sample of initializing 3D Secure 2.
     * This should be call before start the [AuthorizingPaymentActivity].
     */
    private fun initializeAuthoringPaymentConfig() {
        val uiCustomization = UiCustomization.Builder()
                .labelCustomization(UiCustomization.LabelCustomization.Builder()
                        .textFontName("fonts/RobotoMono-Regular.ttf")
                        .textFontColor("#000000")
                        .textFontSize(16)
                        .headingTextColor("#000000")
                        .headingTextFontName("fonts/RobotoMono-Bold.ttf")
                        .headingTextFontSize(20)
                        .build())
                .textBoxCustomization(UiCustomization.TextBoxCustomization.Builder()
                        .textFontName("fonts/RobotoMono-Regular.ttf")
                        .textFontColor("#000000")
                        .textFontSize(16)
                        .borderWidth(2)
                        .cornerRadius(8)
                        .borderColor("#FF0000")
                        .build())
                .toolbarCustomization(UiCustomization.ToolbarCustomization.Builder()
                        .textFontName("fonts/RobotoMono-Bold.ttf")
                        .textFontColor("#000000")
                        .textFontSize(20)
                        .backgroundColor("#FFFFFF")
                        .headerText("Secure Checkout")
                        .buttonText("Close")
                        .build())
                .buttonCustomization(UiCustomization.ButtonType.SUBMIT_BUTTON, UiCustomization.ButtonCustomization.Builder()
                        .textFontName("fonts/RobotoMono-Bold.ttf")
                        .textFontColor("#FFFFFF")
                        .textFontSize(20)
                        .backgroundColor("#FF0000")
                        .cornerRadius(8)
                        .build())
                .buttonCustomization(UiCustomization.ButtonType.RESEND_BUTTON, UiCustomization.ButtonCustomization.Builder()
                        .textFontName("fonts/RobotoMono-Bold.ttf")
                        .textFontColor("#000000")
                        .textFontSize(20)
                        .backgroundColor("#FFFFFF")
                        .cornerRadius(8)
                        .build())
                .build()

        val threeDSConfig = ThreeDSConfig.Builder()
                .uiCustomization(uiCustomization)
                .timeout(5)
                .build()
        val authPaymentConfig = AuthorizingPaymentConfig.Builder()
                .threeDSConfig(threeDSConfig)
                .build()
        AuthorizingPaymentConfig.initialize(authPaymentConfig)
    }

    private fun showAuthorizingPaymentDialog() {
        val marginSize = resources.getDimensionPixelSize(R.dimen.medium_margin)
        val authorizeUrlEditText = EditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(marginSize, marginSize, marginSize, marginSize)
            }

            hint = "Authorize URL"
        }
        val returnUrlEditText = EditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(marginSize, marginSize, marginSize, marginSize)
            }
            hint = "Return URL"
        }
        val containerView = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.VERTICAL
            addView(authorizeUrlEditText)
            addView(returnUrlEditText)
        }
        AlertDialog.Builder(this)
                .setTitle("Authorizing Payment")
                .setView(containerView)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    authorizeUrl(authorizeUrlEditText.text.toString(), returnUrlEditText.text.toString())
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
    }

    private fun authorizeUrl(authorizeUrl: String, returnUrl: String) {
        Log.d(TAG, """
            authorizeUrl=$authorizeUrl
            returnUrl=$returnUrl
        """.trimIndent())
        Intent(this, AuthorizingPaymentActivity::class.java).run {
            putExtra(EXTRA_AUTHORIZED_URLSTRING, authorizeUrl)
            putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf(returnUrl))
            startActivityForResult(this, AUTHORIZING_PAYMENT_REQUEST_CODE)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
                with(data.getParcelableExtra<AuthoringPaymentResult>(AuthorizingPaymentActivity.EXTRA_AUTHORIZING_PAYMENT_RESULT)) {
                    Log.d(TAG, this.toString())
                    val resultMessage = when (this) {
                        is AuthoringPaymentResult.ThreeDS1Completed -> "Authorization with 3D Secure version 1 completed: returnedUrl=${this.returnedUrl}"
                        is AuthoringPaymentResult.ThreeDS2Completed -> "Authorization with 3D Secure version 2 completed: transStatus=${this.transStatus}"
                        is AuthoringPaymentResult.Failure -> "Authorization with 3D Secure failed: ${this.errorMessage}"
                        null -> "Not found the authorization result."
                    }
                    Log.d(TAG, resultMessage)
                    snackbar.setText(resultMessage).show()
                }
            }
            PAYMENT_CREATOR_REQUEST_CODE -> {
                if (data.hasExtra(OmiseActivity.EXTRA_SOURCE_OBJECT)) {
                    val source = data.getParcelableExtra<Source>(OmiseActivity.EXTRA_SOURCE_OBJECT)
                    snackbar.setText(source?.id ?: "No source object.").show()
                } else if (data.hasExtra(OmiseActivity.EXTRA_TOKEN)) {
                    val token = data.getParcelableExtra<Token>(OmiseActivity.EXTRA_TOKEN_OBJECT)
                    snackbar.setText(token?.id ?: "No token object.").show()
                }
            }
            CREDIT_CARD_REQUEST_CODE -> {
                val token = data.getParcelableExtra<Token>(OmiseActivity.EXTRA_TOKEN_OBJECT)
                snackbar.setText(token?.id ?: "No token object.").show()
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}
