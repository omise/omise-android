package co.omise.android.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import co.omise.android.R
import co.omise.android.api.Client
import co.omise.android.api.RequestListener
import co.omise.android.models.APIError
import co.omise.android.models.Token
import kotlinx.android.synthetic.main.activity_credit_card.button_submit
import kotlinx.android.synthetic.main.activity_credit_card.edit_card_name
import kotlinx.android.synthetic.main.activity_credit_card.edit_card_number
import kotlinx.android.synthetic.main.activity_credit_card.edit_expiry_date
import kotlinx.android.synthetic.main.activity_credit_card.edit_security_code
import kotlinx.android.synthetic.main.activity_credit_card.text_error_message
import java.io.IOError

class CreditCardActivity : AppCompatActivity() {

    private val cardNumberEdit: CreditCardEditText by lazy { edit_card_number }
    private val cardNameEdit: CardNameEditText by lazy { edit_card_name }
    private val expiryDateEdit: ExpiryDateEditText by lazy { edit_expiry_date }
    private val securityCodeEdit: SecurityCodeEditText by lazy { edit_security_code }
    private val submitButton: Button by lazy { button_submit }
    private val errorMessageText: TextView by lazy { text_error_message }

    private val editTexts: List<OmiseEditText> by lazy {
        listOf(cardNumberEdit, cardNameEdit, expiryDateEdit, securityCodeEdit)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_card)
        setTitle(R.string.default_form_title)

        submitButton.setOnClickListener(::submit)

        editTexts.forEach {
            it.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    try {
                        it.validate()
                    } catch (e: InputValidationException.InvalidInputException) {
                        it.errorMessage = getString(R.string.error_invalid, it.hint)
                    } catch (e: InputValidationException.EmptyInputException) {
                        it.errorMessage = null
                    }
                } else {
                    it.errorMessage = null
                }
            }

            it.setOnAfterTextChangeListener(::updateSubmitButton)
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        super.onBackPressed()
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

            errorMessageText.visibility = View.VISIBLE

            val message = when (throwable) {
                is IOError -> getString(R.string.error_io, throwable.message)
                is APIError -> getString(R.string.error_api, throwable.errorMessage)
                else -> getString(R.string.error_unknown, throwable.message)
            }

            errorMessageText.text = message
        }
    }

    private fun disableForm() {
        setFormEnabled(false)
    }

    private fun enableForm() {
        setFormEnabled(true)
    }

    private fun setFormEnabled(enabled: Boolean) {
        editTexts.forEach { it.isEnabled = enabled }
        submitButton.isEnabled = enabled
    }

    private fun submit() {
        val number = cardNumberEdit.cardNumber
        val name = cardNameEdit.cardName
        val expiryMonth = expiryDateEdit.expiryMonth
        val expiryYear = expiryDateEdit.expiryYear
        val securityCode = securityCodeEdit.securityCode

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

    private fun updateSubmitButton() {
        val isFormValid = editTexts.map { it.isValid }
                .reduce { acc, b -> acc && b }
        submitButton.isEnabled = isFormValid
    }

    companion object {
        // input
        const val EXTRA_PKEY = "CreditCardActivity.publicKey"

        const val EXTRA_TOKEN = "CreditCardActivity.token"
        const val EXTRA_TOKEN_OBJECT = "CreditCardActivity.tokenObject"
        const val EXTRA_CARD_OBJECT = "CreditCardActivity.cardObject"
    }
}

fun View.setOnClickListener(action: () -> Unit) {
    this.setOnClickListener { action() }
}

fun EditText.setOnAfterTextChangeListener(action: () -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            action()
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
    })
}
