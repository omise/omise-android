package co.omise.android.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
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
import co.omise.android.models.CountryInfo
import co.omise.android.models.Token
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_credit_card.billing_address_container
import kotlinx.android.synthetic.main.activity_credit_card.button_security_code_tooltip
import kotlinx.android.synthetic.main.activity_credit_card.button_submit
import kotlinx.android.synthetic.main.activity_credit_card.edit_card_name
import kotlinx.android.synthetic.main.activity_credit_card.edit_card_number
import kotlinx.android.synthetic.main.activity_credit_card.edit_city
import kotlinx.android.synthetic.main.activity_credit_card.edit_country
import kotlinx.android.synthetic.main.activity_credit_card.edit_expiry_date
import kotlinx.android.synthetic.main.activity_credit_card.edit_postal_code
import kotlinx.android.synthetic.main.activity_credit_card.edit_security_code
import kotlinx.android.synthetic.main.activity_credit_card.edit_state
import kotlinx.android.synthetic.main.activity_credit_card.edit_street1
import kotlinx.android.synthetic.main.activity_credit_card.scrollview
import kotlinx.android.synthetic.main.activity_credit_card.text_card_name_error
import kotlinx.android.synthetic.main.activity_credit_card.text_card_number_error
import kotlinx.android.synthetic.main.activity_credit_card.text_city_error
import kotlinx.android.synthetic.main.activity_credit_card.text_country_error
import kotlinx.android.synthetic.main.activity_credit_card.text_expiry_date_error
import kotlinx.android.synthetic.main.activity_credit_card.text_postal_code_error
import kotlinx.android.synthetic.main.activity_credit_card.text_security_code_error
import kotlinx.android.synthetic.main.activity_credit_card.text_state_error
import kotlinx.android.synthetic.main.activity_credit_card.text_street1_error
import java.io.IOError
import java.util.Locale

/**
 * CreditCardActivity is the UI class for taking credit card information input from the user.
 */
class CreditCardActivity : OmiseActivity() {

    private lateinit var pKey: String
    private val cardNumberEdit: CreditCardEditText by lazy { edit_card_number }
    private val cardNameEdit: CardNameEditText by lazy { edit_card_name }
    private val expiryDateEdit: ExpiryDateEditText by lazy { edit_expiry_date }
    private val securityCodeEdit: SecurityCodeEditText by lazy { edit_security_code }
    private val countryEdit: OmiseEditText by lazy { edit_country }
    private val street1Edit: OmiseEditText by lazy { edit_street1 }
    private val cityEdit: OmiseEditText by lazy { edit_city }
    private val stateEdit: OmiseEditText by lazy { edit_state }
    private val postalCodeEdit: OmiseEditText by lazy { edit_postal_code }

    private val submitButton: Button by lazy { button_submit }
    private val scrollView: ScrollView by lazy { scrollview }
    private val cardNumberErrorText: TextView by lazy { text_card_number_error }
    private val cardNameErrorText: TextView by lazy { text_card_name_error }
    private val expiryDateErrorText: TextView by lazy { text_expiry_date_error }
    private val securityCodeErrorText: TextView by lazy { text_security_code_error }
    private val countryErrorText: TextView by lazy { text_country_error }
    private val street1ErrorText: TextView by lazy { text_street1_error }
    private val cityErrorText: TextView by lazy { text_city_error }
    private val stateErrorText: TextView by lazy { text_state_error }
    private val postalCodeErrorText: TextView by lazy { text_postal_code_error }

    private val securityCodeTooltipButton: ImageButton by lazy { button_security_code_tooltip }

    private val billingAddressContainer: LinearLayout by lazy { billing_address_container }

    private val avsCountries = CountryInfo.ALL.filter { listOf("US", "GB", "CA").contains(it.code) }

    private val editTexts: Map<OmiseEditText, TextView> by lazy {
        mapOf(
            cardNumberEdit to cardNumberErrorText,
            cardNameEdit to cardNameErrorText,
            expiryDateEdit to expiryDateErrorText,
            securityCodeEdit to securityCodeErrorText,
            countryEdit to countryErrorText,
            street1Edit to street1ErrorText,
            cityEdit to cityErrorText,
            stateEdit to stateErrorText,
            postalCodeEdit to postalCodeErrorText
        )
    }

    private val billingAddressEditTexts: Map<OmiseEditText, TextView> by lazy {
        mapOf(
            countryEdit to countryErrorText,
            street1Edit to street1ErrorText,
            cityEdit to cityErrorText,
            stateEdit to stateErrorText,
            postalCodeEdit to postalCodeErrorText
        )
    }

    private var selectedCountry: CountryInfo? = null
        set(value) {
            field = value
            value?.let {
                countryEdit.setText(it.displayName)
                invalidateBillingAddressForm()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_credit_card)

        initialize()

        setTitle(R.string.default_form_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        submitButton.setOnClickListener(::submit)
        securityCodeTooltipButton.setOnClickListener(::showSecurityCodeTooltipDialog)
        countryEdit.setOnClickListener(::showCountryDropdownDialog)

        editTexts.forEach { (editText, errorText) ->
            editText.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    try {
                        editText.validate()
                    } catch (e: InputValidationException.InvalidInputException) {
                        errorText.text = editText.getErrorMessage()
                    } catch (e: InputValidationException.EmptyInputException) {
                        if (isBillingAddressRequired()) {
                            errorText.text = editText.getErrorMessage()
                        } else {
                            errorText.text = null
                        }
                    }
                } else {
                    errorText.text = null
                }
            }
            editText.setOnAfterTextChangeListener(::updateSubmitButton)
        }

        selectedCountry = CountryInfo.ALL.find { it.code == Locale.getDefault().country }
    }

    private fun EditText.getErrorMessage(): String? {
        return when (this) {
            cardNumberEdit -> getString(R.string.error_invalid_card_number)
            cardNameEdit -> getString(R.string.error_invalid_card_name)
            expiryDateEdit -> getString(R.string.error_invalid_expiration_date)
            securityCodeEdit -> getString(R.string.error_invalid_security_code)
            street1Edit -> getString(R.string.error_required_street1)
            cityEdit -> getString(R.string.error_required_city)
            stateEdit -> getString(R.string.error_required_state)
            postalCodeEdit -> getString(R.string.error_required_postal_code)
            else -> null
        }
    }

    private fun initialize() {
        require(intent.hasExtra(EXTRA_PKEY)) { "Could not found ${::EXTRA_PKEY.name}." }
        pKey = requireNotNull(intent.getStringExtra(EXTRA_PKEY)) { "${::EXTRA_PKEY.name} must not be null." }
        invalidateBillingAddressForm()
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
        val cardParam = CardParam(
            name = cardNameEdit.cardName,
            number = cardNumberEdit.cardNumber,
            expirationMonth = expiryDateEdit.expiryMonth,
            expirationYear = expiryDateEdit.expiryYear,
            securityCode = securityCodeEdit.securityCode,
            country = selectedCountry?.code,
            street1 = street1Edit.text.toString(),
            city = cityEdit.text.toString(),
            state = stateEdit.text.toString(),
            postalCode = postalCodeEdit.text.toString(),
        )

        val request =
            Token.CreateTokenRequestBuilder(cardParam).build()

        disableForm()

        val listener = CreateTokenRequestListener()
        try {
            Client(pKey).send(request, listener)
        } catch (ex: Exception) {
            listener.onRequestFailed(ex)
        }
    }

    private fun updateSubmitButton() {
        val fields = if (isBillingAddressRequired()) editTexts else editTexts.filterKeys { !billingAddressEditTexts.contains(it) }
        val isFormValid = fields
            .map { (editText, _) -> editText.isValid }
            .reduce { acc, b -> acc && b }
        submitButton.isEnabled = isFormValid
    }

    private fun showSecurityCodeTooltipDialog() {
        val brand = CardNumber.brand(cardNumberEdit.cardNumber)
        val dialog = SecurityCodeTooltipDialogFragment.newInstant(brand)
        dialog.show(supportFragmentManager, null)
    }

    private fun showCountryDropdownDialog() {
        val dialog = CountryListDialogFragment.newInstant()
        dialog.listener = object : CountryListDialogFragment.CountryListDialogListener {
            override fun onCountrySelected(country: CountryInfo) {
                selectedCountry = country
            }
        }
        dialog.show(supportFragmentManager, null)
    }

    private fun invalidateBillingAddressForm() {
        billingAddressContainer.visibility = if (isBillingAddressRequired()) View.VISIBLE else View.GONE
        billingAddressEditTexts.forEach { (editText, errorText) ->
            if (editText != countryEdit) {
                editText.text = null
                errorText.text = null
            }
        }
    }

    private fun isBillingAddressRequired(): Boolean {
        return selectedCountry != null && avsCountries.contains(selectedCountry)
    }
}
