package co.omise.android.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import co.omise.android.CardNumber
import co.omise.android.R
import co.omise.android.api.Client
import co.omise.android.api.RequestListener
import co.omise.android.extensions.getMessageFromResources
import co.omise.android.extensions.setOnAfterTextChangeListener
import co.omise.android.extensions.setOnClickListener
import co.omise.android.models.APIError
import co.omise.android.models.CardParam
import co.omise.android.models.Token
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_credit_card.button_security_code_tooltip
import kotlinx.android.synthetic.main.activity_credit_card.button_submit
import kotlinx.android.synthetic.main.activity_credit_card.edit_card_name
import kotlinx.android.synthetic.main.activity_credit_card.edit_card_number
import kotlinx.android.synthetic.main.activity_credit_card.edit_expiry_date
import kotlinx.android.synthetic.main.activity_credit_card.edit_security_code
import kotlinx.android.synthetic.main.activity_credit_card.text_card_name_error
import kotlinx.android.synthetic.main.activity_credit_card.text_card_number_error
import kotlinx.android.synthetic.main.activity_credit_card.text_expiry_date_error
import kotlinx.android.synthetic.main.activity_credit_card.text_security_code_error
import kotlinx.android.synthetic.main.activity_credit_card.scrollview
import java.io.IOError

class CreditCardActivity : OmiseActivity() {

    private val cardNumberEdit: CreditCardEditText by lazy { edit_card_number }
    private val cardNameEdit: CardNameEditText by lazy { edit_card_name }
    private val expiryDateEdit: ExpiryDateEditText by lazy { edit_expiry_date }
    private val securityCodeEdit: SecurityCodeEditText by lazy { edit_security_code }
    private val submitButton: Button by lazy { button_submit }
    private val scrollView: ScrollView by lazy { scrollview }
    private val cardNumberErrorText: TextView by lazy { text_card_number_error }
    private val cardNameErrorText: TextView by lazy { text_card_name_error }
    private val expiryDateErrorText: TextView by lazy { text_expiry_date_error }
    private val securityCodeErrorText: TextView by lazy { text_security_code_error }
    private val securityCodeTooltipButton: ImageButton by lazy { button_security_code_tooltip }

    private val editTexts: Map<OmiseEditText, TextView> by lazy {
        mapOf(
                cardNumberEdit to cardNumberErrorText,
                cardNameEdit to cardNameErrorText,
                expiryDateEdit to expiryDateErrorText,
                securityCodeEdit to securityCodeErrorText
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!intent.hasExtra(EXTRA_PKEY)) {
            throw IllegalAccessException("Can not found ${::EXTRA_PKEY.name}.")
        }

        setContentView(R.layout.activity_credit_card)
        setTitle(R.string.default_form_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        submitButton.setOnClickListener(::submit)
        securityCodeTooltipButton.setOnClickListener(::showSecurityCodeTooltipDialog)

        editTexts.forEach { (editText, errorText) ->
            editText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    try {
                        editText.validate()
                    } catch (e: InputValidationException.InvalidInputException) {
                        errorText.text = when (editText) {
                            cardNumberEdit -> getString(R.string.error_invalid_card_number)
                            cardNameEdit -> getString(R.string.error_invalid_card_name)
                            expiryDateEdit -> getString(R.string.error_invalid_expiration_date)
                            securityCodeEdit -> getString(R.string.error_invalid_security_code)
                            else -> null
                        }
                    } catch (e: InputValidationException.EmptyInputException) {
                        errorText.text = null
                    }
                } else {
                    errorText.text = null
                }
            }
            editText.setOnAfterTextChangeListener(::updateSubmitButton)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        super.onBackPressed()
    }

    private inner class CreateTokenRequestListener : RequestListener<Token> {

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

            val message = when (throwable) {
                is IOError -> getString(R.string.error_io, throwable.message)
                is APIError -> throwable.getMessageFromResources(resources)
                else -> getString(R.string.error_unknown, throwable.message)
            }

            Snackbar.make(scrollView, message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun disableForm() {
        setFormEnabled(false)
    }

    private fun enableForm() {
        setFormEnabled(true)
    }

    private fun setFormEnabled(enabled: Boolean) {
        editTexts.forEach { (editText, _) -> editText.isEnabled = enabled }
        submitButton.isEnabled = enabled
    }

    private fun submit() {
        val number = cardNumberEdit.cardNumber
        val name = cardNameEdit.cardName
        val expiryMonth = expiryDateEdit.expiryMonth
        val expiryYear = expiryDateEdit.expiryYear
        val securityCode = securityCodeEdit.securityCode

        val cardParam = CardParam(
                name = name,
                number = number,
                expirationMonth = expiryMonth,
                expirationYear = expiryYear,
                securityCode = securityCode)

        val request =
                Token.CreateTokenRequestBuilder(cardParam).build()

        disableForm()

        val pKey = intent.getStringExtra(EXTRA_PKEY)
        val listener = CreateTokenRequestListener()
        try {
            Client(pKey).send(request, listener)
        } catch (ex: Exception) {
            listener.onRequestFailed(ex)
        }
    }

    private fun updateSubmitButton() {
        val isFormValid = editTexts.map { (editText, _) -> editText.isValid }
                .reduce { acc, b -> acc && b }
        submitButton.isEnabled = isFormValid
    }

    private fun showSecurityCodeTooltipDialog() {
        val brand = CardNumber.brand(cardNumberEdit.cardNumber)
        val dialog = SecurityCodeTooltipDialogFragment.newInstant(brand)
        dialog.show(supportFragmentManager, null)
    }
}
