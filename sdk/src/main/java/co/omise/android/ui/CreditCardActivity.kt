package co.omise.android.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import co.omise.android.R
import co.omise.android.api.Client
import co.omise.android.api.RequestListener
import co.omise.android.extensions.getMessageFromResources
import co.omise.android.extensions.setOnAfterTextChangeListener
import co.omise.android.extensions.setOnClickListener
import co.omise.android.models.APIError
import co.omise.android.models.CardBrand
import co.omise.android.models.Token
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_credit_card.button_security_code_tooltip
import kotlinx.android.synthetic.main.activity_credit_card.button_submit
import kotlinx.android.synthetic.main.activity_credit_card.edit_card_name
import kotlinx.android.synthetic.main.activity_credit_card.edit_card_number
import kotlinx.android.synthetic.main.activity_credit_card.edit_expiry_date
import kotlinx.android.synthetic.main.activity_credit_card.edit_security_code
import kotlinx.android.synthetic.main.activity_credit_card.layout_credit_card_form
import kotlinx.android.synthetic.main.activity_credit_card.text_card_name_error
import kotlinx.android.synthetic.main.activity_credit_card.text_card_number_error
import kotlinx.android.synthetic.main.activity_credit_card.text_expiry_date_error
import kotlinx.android.synthetic.main.activity_credit_card.text_security_code_error
import java.io.IOError

class CreditCardActivity : AppCompatActivity() {

    private val cardNumberEdit: CreditCardEditText by lazy { edit_card_number }
    private val cardNameEdit: CardNameEditText by lazy { edit_card_name }
    private val expiryDateEdit: ExpiryDateEditText by lazy { edit_expiry_date }
    private val securityCodeEdit: SecurityCodeEditText by lazy { edit_security_code }
    private val submitButton: Button by lazy { button_submit }
    private val containerLayout: LinearLayout by lazy { layout_credit_card_form }
    private val cardNumberErrorText: TextView by lazy { text_card_number_error }
    private val cardNameErrorText: TextView by lazy { text_card_name_error }
    private val expiryDateErrorText: TextView by lazy { text_expiry_date_error }
    private val securityCodeErrorText: TextView by lazy { text_security_code_error }
    private val securityCodeTooltipButton: ImageButton by lazy { button_security_code_tooltip }

    private val editTexts: List<Pair<OmiseEditText, TextView>> by lazy {
        listOf(
                Pair(cardNumberEdit, cardNumberErrorText),
                Pair(cardNameEdit, cardNameErrorText),
                Pair(expiryDateEdit, expiryDateErrorText),
                Pair(securityCodeEdit, securityCodeErrorText)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!intent.hasExtra(EXTRA_PKEY)) {
            throw IllegalAccessException("Can not found ${::EXTRA_PKEY.name}.")
        }

        setContentView(R.layout.activity_credit_card)
        setTitle(R.string.default_form_title)

        submitButton.setOnClickListener(::submit)
        securityCodeTooltipButton.setOnClickListener(::showSecurityCodeTooltipDialog)

        editTexts.forEach {
            it.first.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    try {
                        it.first.validate()
                    } catch (e: InputValidationException.InvalidInputException) {
                        it.second.text = when (it.first) {
                            cardNumberEdit -> getString(R.string.error_invalid_card_number)
                            cardNameEdit -> getString(R.string.error_invalid_card_name)
                            expiryDateEdit -> getString(R.string.error_invalid_expiry_date)
                            securityCodeEdit -> getString(R.string.error_invalid_security_code)
                            else -> null
                        }
                    } catch (e: InputValidationException.EmptyInputException) {
                        it.second.text = null
                    }
                } else {
                    it.second.text = null
                }
            }
            it.first.setOnAfterTextChangeListener(::updateSubmitButton)
        }
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

            Snackbar.make(containerLayout, message, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun disableForm() {
        setFormEnabled(false)
    }

    private fun enableForm() {
        setFormEnabled(true)
    }

    private fun setFormEnabled(enabled: Boolean) {
        editTexts.forEach { it.first.isEnabled = enabled }
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
        val listener = CreateTokenRequestListener()
        try {
            Client(pkey).send(request, listener)
        } catch (ex: Exception) {
            listener.onRequestFailed(ex)
        }
    }

    private fun updateSubmitButton() {
        val isFormValid = editTexts.map { it.first.isValid }
                .reduce { acc, b -> acc && b }
        submitButton.isEnabled = isFormValid
    }

    private fun showSecurityCodeTooltipDialog() {
        val dialog = SecurityCodeTooltipDialogFragment.newInstant(CardBrand.VISA)
        dialog.show(supportFragmentManager, null)
    }

    companion object {
        // input
        const val EXTRA_PKEY = "CreditCardActivity.publicKey"

        const val EXTRA_TOKEN = "CreditCardActivity.token"
        const val EXTRA_TOKEN_OBJECT = "CreditCardActivity.tokenObject"
        const val EXTRA_CARD_OBJECT = "CreditCardActivity.cardObject"
    }
}
