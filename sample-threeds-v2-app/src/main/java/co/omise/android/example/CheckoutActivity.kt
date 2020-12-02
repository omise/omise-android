package co.omise.android.example

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import co.omise.android.api.Client
import co.omise.android.api.RequestListener
import co.omise.android.models.Capability
import co.omise.android.models.Token
import co.omise.android.ui.CreditCardActivity
import co.omise.android.ui.OmiseActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_checkout.amount_edit
import kotlinx.android.synthetic.main.activity_checkout.checkout_button
import kotlinx.android.synthetic.main.activity_checkout.currency_edit
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    companion object {
        private const val CHECKOUT_REQUEST_CODE = 0x3D8
    }

    private val amountEdit: EditText by lazy { amount_edit }
    private val snackbar: Snackbar by lazy {
        Snackbar.make(findViewById(R.id.content), "", Snackbar.LENGTH_SHORT)
    }

    private var capability: Capability? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        supportActionBar?.title = getString(R.string.activity_checkout)

        checkout_button.setOnClickListener { checkout() }


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

    private fun checkout() {
        Intent(this, CreditCardActivity::class.java).run {
            putExtra(OmiseActivity.EXTRA_PKEY, PUBLIC_KEY)
            startActivityForResult(this, CHECKOUT_REQUEST_CODE)
        }
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
            CHECKOUT_REQUEST_CODE -> {
                Intent(this, PaymentProcessingActivity::class.java).run {
                    val token = data.getParcelableExtra<Token>(OmiseActivity.EXTRA_TOKEN_OBJECT)
                    putExtra(OmiseActivity.EXTRA_TOKEN, token?.id)
                    putExtra(OmiseActivity.EXTRA_AMOUNT, getAmount())
                    putExtra(OmiseActivity.EXTRA_CURRENCY, currency_edit.text.toString())
                    startActivity(this)
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun  getAmount(): Long {
        var amount = amountEdit.text.toString().toDouble()
        if (currency_edit.text.toString().toLowerCase(Locale.getDefault()) != "jpy") {
            amount *= 100
        }
        return amount.toLong()
    }
}
