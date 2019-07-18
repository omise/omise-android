package co.omise.android.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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
import java.io.IOError

class CreditCardActivity : AppCompatActivity() {

    private val views = Views(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_card)
        setTitle(R.string.default_form_title)

        views.spinner(R.id.spinner_expiry_month).adapter = ExpiryMonthSpinnerAdapter()
        views.spinner(R.id.spinner_expiry_year).adapter = ExpiryYearSpinnerAdapter()
        views.editText(R.id.edit_card_number).addTextChangedListener(ActivityTextWatcher())
        views.button(R.id.button_submit).setOnClickListener(ActivityOnClickListener())
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

    private inner class ActivityTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            val pan = s.toString()
            if (pan.length > 6) {
                val brand = CardNumber.brand(pan)
                if (brand != null && brand.logoResourceId > -1) {
                    views.image(R.id.image_card_brand).setImageResource(brand.logoResourceId)
                    return
                }
            }

            views.image(R.id.image_card_brand).setImageDrawable(null)
        }
    }

    private inner class ActivityOnClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            if (v.id == R.id.button_submit) {
                submit()
            }
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

            val textView = views.textView(R.id.text_error_message)
            textView.visibility = View.VISIBLE

            val message = when (throwable) {
                is IOError -> getString(R.string.error_io, throwable.message)
                is APIError -> getString(R.string.error_api, throwable.errorMessage)
                else -> getString(R.string.error_unknown, throwable.message)
            }

            textView.text = message
        }
    }

    private fun disableForm() {
        setFormEnabled(false)
    }

    private fun enableForm() {
        setFormEnabled(true)
    }

    private fun setFormEnabled(enabled: Boolean) {
        views.editText(R.id.edit_card_number).isEnabled = enabled
        views.editText(R.id.edit_card_name).isEnabled = enabled
        views.editText(R.id.edit_security_code).isEnabled = enabled
        views.spinner(R.id.spinner_expiry_month).isEnabled = enabled
        views.spinner(R.id.spinner_expiry_year).isEnabled = enabled
        views.button(R.id.button_submit).isEnabled = enabled
        invalidateOptionsMenu()
    }

    private fun applyCardIOResult(data: CreditCard) {
        val numberField = views.editText(R.id.edit_card_number)
        val nameField = views.editText(R.id.edit_card_name)
        val securityCodeField = views.editText(R.id.edit_security_code)

        if (data.cardNumber != null && data.cardNumber.isNotEmpty()) {
            numberField.setText(CardNumber.format(data.cardNumber))
        }

        if (data.cardholderName != null && data.cardholderName.isNotEmpty()) {
            nameField.setText(data.cardholderName)
        }

        if (data.isExpiryValid) {
            var spinner = views.spinner(R.id.spinner_expiry_month)
            val monthAdapter = spinner.adapter as ExpiryMonthSpinnerAdapter
            spinner.setSelection(monthAdapter.getPosition(data.expiryMonth))

            spinner = views.spinner(R.id.spinner_expiry_year)
            val yearAdapter = spinner.adapter as ExpiryYearSpinnerAdapter
            spinner.setSelection(yearAdapter.getPosition(data.expiryYear))
        }

        if (data.cvv != null && data.cvv.isNotEmpty()) {
            securityCodeField.setText(data.cvv)
        }

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (numberField.text == null || numberField.text.toString().isEmpty()) {
            numberField.requestFocus()
            imm.showSoftInput(numberField, InputMethodManager.SHOW_IMPLICIT)
        } else if (nameField.text == null || nameField.text.toString().isEmpty()) {
            nameField.requestFocus()
            imm.showSoftInput(nameField, InputMethodManager.SHOW_IMPLICIT)
        } else if (securityCodeField.text == null || securityCodeField.text.toString().isEmpty()) {
            securityCodeField.requestFocus()
            imm.showSoftInput(securityCodeField, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun submit() {
        val numberField = views.editText(R.id.edit_card_number)
        val nameField = views.editText(R.id.edit_card_name)
        val securityCodeField = views.editText(R.id.edit_security_code)

        val valid = validateNonEmpty(numberField) and
                validateNonEmpty(nameField) and
                validateNonEmpty(securityCodeField) and
                validateLuhn(numberField)
        if (!valid) {
            return
        }

        val expiryMonth = views.spinner(R.id.spinner_expiry_month).selectedItem as Int
        val expiryYear = views.spinner(R.id.spinner_expiry_year).selectedItem as Int

        val number = numberField.text.toString()
        val name = nameField.text.toString()
        val securityCode = securityCodeField.text.toString()

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

    private fun validateNonEmpty(field: EditText): Boolean {
        val value = field.text.toString().trim { it <= ' ' }
        if (value.isEmpty()) {
            field.error = String.format(getString(R.string.error_required), field.hint)
            return false
        }

        return true
    }

    private fun validateLuhn(field: EditText): Boolean {
        val value = field.text.toString().trim { it <= ' ' }
        if (!CardNumber.luhn(value)) {
            field.error = String.format(getString(R.string.error_invalid), field.hint)
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
