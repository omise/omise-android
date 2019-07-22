package co.omise.android.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
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

    private val editTexts: List<OmiseEditText> by lazy {
        listOf(edit_card_number, edit_card_name, edit_expiry_date, edit_security_code)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_card)
        setTitle(R.string.default_form_title)

        button_submit.setOnClickListener(::submit)

        editTexts.forEach {
            it.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && it.validate().contains(InvalidationType.Invalid)) {
                    it.errorMessage = getString(R.string.error_invalid, it.hint)
                } else {
                    it.errorMessage = null
                }
            }
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
        editTexts.forEach { it.isEnabled = enabled }
    }

    private fun submit() {
        editTexts.forEach {
            if (it.validate().isNotEmpty()) {
                return@submit
            }
        }

        val number = edit_card_number.text.toString()
        val name = edit_card_name.text.toString()
        val expiryMonth = edit_expiry_date.expiryMonth
        val expiryYear = edit_expiry_date.expiryYear
        val securityCode = edit_security_code.text.toString()

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
