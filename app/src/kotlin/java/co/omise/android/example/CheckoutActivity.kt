package co.omise.android.example

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_AUTHORIZED_URLSTRING
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS
import co.omise.android.AuthorizingPaymentURLVerifier.Companion.EXTRA_RETURNED_URLSTRING
import co.omise.android.api.Client
import co.omise.android.api.RequestListener
import co.omise.android.models.Amount
import co.omise.android.models.Capability
import co.omise.android.models.Source
import co.omise.android.models.Token
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

        supportActionBar?.title = getString(R.string.activity_checkout)

        choosePaymentMethodButton.setOnClickListener { choosePaymentMethod() }
        creditCardButton.setOnClickListener { payByCreditCard() }
        authorizeUrlButton.setOnClickListener { authorizeUrl() }

        val client = Client(PUBLIC_KEY)
        val request = Capability.GetCapabilitiesRequestBuilder().build()
        client.send(request, object : RequestListener<Capability> {
            override fun onRequestSucceed(model: Capability) {
                capability = model
            }

            override fun onRequestFailed(throwable: Throwable) {
                snackbar.setText(throwable.message.orEmpty()).show()
            }
        })
    }

    private fun choosePaymentMethod() {
        val isUsedSpecificsPaymentMethods = PaymentSetting.isUsedSpecificsPaymentMethods(this)

        if (!isUsedSpecificsPaymentMethods && capability == null) {
            snackbar.setText("Capability have not set yet.")
            return
        }

        val localAmount = amountEdit.text.toString().trim().toDouble()
        val currency = currencyEdit.text.toString().trim().toLowerCase()
        val amount = Amount.fromLocalAmount(localAmount, currency)

        val intent = Intent(this@CheckoutActivity, PaymentCreatorActivity::class.java)
        intent.putExtra(OmiseActivity.EXTRA_PKEY, PUBLIC_KEY)
        intent.putExtra(OmiseActivity.EXTRA_AMOUNT, amount.amount)
        intent.putExtra(OmiseActivity.EXTRA_CURRENCY, amount.currency)

        if (isUsedSpecificsPaymentMethods) {
            intent.putExtra(OmiseActivity.EXTRA_CAPABILITY, PaymentSetting.getCapabilityFromSharedPreferences(this))

        } else {
            intent.putExtra(OmiseActivity.EXTRA_CAPABILITY, capability)
        }

        startActivityForResult(intent, PAYMENT_CREATOR_REQUEST_CODE)
    }

    private fun payByCreditCard() {
        val intent = Intent(this, CreditCardActivity::class.java)
        intent.putExtra(OmiseActivity.EXTRA_PKEY, PUBLIC_KEY)
        startActivityForResult(intent, CREDIT_CARD_REQUEST_CODE)
    }

    private fun authorizeUrl() {
        val intent = Intent(this, AuthorizingPaymentActivity::class.java)
        intent.putExtra(EXTRA_AUTHORIZED_URLSTRING, "https://pay.omise.co/offsites/")
        intent.putExtra(EXTRA_EXPECTED_RETURN_URLSTRING_PATTERNS, arrayOf("http://www.example.com"))
        startActivityForResult(intent, AUTHORIZING_PAYMENT_REQUEST_CODE)
    }

    private fun openPaymentSetting() {
        val intent = Intent(this, PaymentSettingActivity::class.java)
        startActivity(intent)
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
            snackbar.setText("Payment success but no result.").show()
            return
        }

        when (requestCode) {
            AUTHORIZING_PAYMENT_REQUEST_CODE -> {
                val url = data.getStringExtra(EXTRA_RETURNED_URLSTRING)
                snackbar.setText(url).show()
            }
            PAYMENT_CREATOR_REQUEST_CODE -> {
                if (data.hasExtra(OmiseActivity.EXTRA_SOURCE_OBJECT)) {
                    val source = data.getParcelableExtra<Source>(OmiseActivity.EXTRA_SOURCE_OBJECT)
                    snackbar.setText(source.id.orEmpty()).show()
                } else if (data.hasExtra(OmiseActivity.EXTRA_TOKEN)) {
                    val token = data.getParcelableExtra<Token>(OmiseActivity.EXTRA_TOKEN_OBJECT)
                    snackbar.setText(token.id.orEmpty()).show()
                }
            }
            CREDIT_CARD_REQUEST_CODE -> {
                val token = data.getParcelableExtra<Token>(OmiseActivity.EXTRA_TOKEN_OBJECT)
                snackbar.setText(token.id.orEmpty()).show()
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}
