package co.omise.android.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import co.omise.android.CardIO
import co.omise.android.CardNumber
import co.omise.android.R
import co.omise.android.api.Client
import co.omise.android.api.RequestListener
import co.omise.android.models.APIError
import co.omise.android.models.Token
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard
import kotlinx.android.synthetic.main.activity_credit_card.button_submit
import kotlinx.android.synthetic.main.activity_credit_card.edit_card_name
import kotlinx.android.synthetic.main.activity_credit_card.edit_card_number
import kotlinx.android.synthetic.main.activity_credit_card.edit_expiry_date
import kotlinx.android.synthetic.main.activity_credit_card.edit_security_code
import kotlinx.android.synthetic.main.activity_credit_card.text_error_message
import java.io.IOError

class CreditCardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_card)
        setTitle(R.string.default_form_title)

        edit_card_name.errorMessage = "error"
        edit_card_number.errorMessage = "error"
        edit_security_code.errorMessage = "error"
        edit_expiry_date.errorMessage = "error"

        button_submit.setOnClickListener { this.submit() }
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_credit_card, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        //        menu.getItem(0).setVisible(CardIO.isAvailable() && views.button(R.id.button_submit).isEnabled());
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_card_io) {
            if (CardIO.isAvailable()) {
                val intent = CardIO.buildIntent(this)
                startActivityForResult(intent, REQUEST_CODE_CARD_IO)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_CARD_IO) {
            if (data == null || !data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                return
            }

            val scanResult = data.getParcelableExtra<CreditCard>(CardIOActivity.EXTRA_SCAN_RESULT)
            applyCardIOResult(scanResult)

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private inner class ActivityRequestListener : RequestListener<Token> {

        override fun onRequestSucceed(model: Token) {
            val data = Intent()
            data.putExtra(EXTRA_TOKEN, model.id)
            data.putExtra(EXTRA_TOKEN_OBJECT, model)
            data.putExtra(EXTRA_CARD_OBJECT, model.card)

            setResult(Activity.RESULT_OK, data)
            finish()
        }

        override fun onRequestFailed(throwable: Throwable) {
            enableForm()

            text_error_message.visibility = View.VISIBLE

            val message = when (throwable) {
                is IOError -> getString(R.string.error_io, throwable.message)
                is APIError -> getString(R.string.error_api, throwable.errorMessage)
                else -> getString(R.string.error_unknown, throwable.message)
            }

            text_error_message.text = message
        }
    }

    private fun disableForm() {
        setFormEnabled(false)
    }

    private fun enableForm() {
        setFormEnabled(true)
    }

    private fun setFormEnabled(enabled: Boolean) {
        edit_card_number.isEnabled = enabled
        edit_card_name.isEnabled = enabled
        edit_security_code.isEnabled = enabled
        button_submit.isEnabled = enabled


        invalidateOptionsMenu()
    }

    private fun applyCardIOResult(data: CreditCard) {
        if (data.cardNumber != null && data.cardNumber.isNotEmpty()) {
            edit_card_number.setText(CardNumber.format(data.cardNumber))
        }

        if (data.cardholderName != null && data.cardholderName.isNotEmpty()) {
            edit_card_name.setText(data.cardholderName)
        }

        if (data.isExpiryValid) {
            edit_expiry_date.setExpiryDate(data.expiryMonth, data.expiryYear)
        }

        if (data.cvv != null && data.cvv.isNotEmpty()) {
            edit_security_code.setText(data.cvv)
        }

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (edit_card_number.text == null || edit_card_number.text.toString().isEmpty()) {
            edit_card_number.requestFocus()
            imm.showSoftInput(edit_card_number, InputMethodManager.SHOW_IMPLICIT)
        } else if (edit_card_name.text == null || edit_card_name.text.toString().isEmpty()) {
            edit_card_name.requestFocus()
            imm.showSoftInput(edit_card_name, InputMethodManager.SHOW_IMPLICIT)
        } else if (edit_security_code.text == null || edit_security_code.text.toString().isEmpty()) {
            edit_security_code.requestFocus()
            imm.showSoftInput(edit_security_code, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun submit() {
        val valid = validateNonEmpty(edit_card_number) &&
                validateNonEmpty(edit_card_name) &&
                validateNonEmpty(edit_security_code) &&
                validateLuhn(edit_card_number)
        if (!valid) {
            return
        }

        val number = edit_card_number.text.toString()
        val name = edit_card_name.text.toString()
        val expiryMonth = edit_expiry_date.expiryMonth
        val expiryYear = edit_expiry_date.expiryYear
        val securityCode = edit_security_code.text.toString()

        Log.d("inputs", """
            $number
            $name
            $expiryMonth/$expiryYear
            $securityCode
        """.trimIndent())

        val request = Token.CreateTokenRequestBuilder(
                name,
                number,
                expiryMonth,
                expiryYear,
                securityCode
        ).build()

        disableForm()

        val pkey = intent.getStringExtra(EXTRA_PKEY)
        val listener = ActivityRequestListener()
        try {
            Client(pkey).send(request, listener)
        } catch (ex: Exception) {
            listener.onRequestFailed(ex)
        }

    }

    private fun validateNonEmpty(field: OmiseEditText): Boolean {
        val value = field.text.toString().trim { it <= ' ' }
        if (value.isEmpty()) {
            field.errorMessage = getString(R.string.error_required, field.hint)
            return false
        }

        return true
    }

    private fun validateLuhn(field: OmiseEditText): Boolean {
        val value = field.text.toString().trim { it <= ' ' }
        if (!CardNumber.luhn(value)) {
            field.errorMessage = getString(R.string.error_invalid, field.hint)
            return false
        }

        return true
    }

    companion object {
        // input
        const val EXTRA_PKEY = "CreditCardActivity.publicKey"
        const val REQUEST_CODE_CARD_IO = 1000

        const val EXTRA_TOKEN = "CreditCardActivity.token"
        const val EXTRA_TOKEN_OBJECT = "CreditCardActivity.tokenObject"
        const val EXTRA_CARD_OBJECT = "CreditCardActivity.cardObject"
    }
}
