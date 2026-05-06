package co.omise.android.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import co.omise.android.CardNumber
import co.omise.android.R
import co.omise.android.api.Client
import co.omise.android.api.Request
import co.omise.android.api.RequestListener
import co.omise.android.databinding.ActivityCreditCardBinding
import co.omise.android.extensions.getMessageFromResources
import co.omise.android.extensions.parcelable
import co.omise.android.extensions.setOnAfterTextChangeListener
import co.omise.android.extensions.setOnClickListener
import co.omise.android.extensions.textOrNull
import co.omise.android.models.APIError
import co.omise.android.models.BackendType
import co.omise.android.models.Capability
import co.omise.android.models.CardHolderDataField
import co.omise.android.models.CardHolderDataList
import co.omise.android.models.CardParam
import co.omise.android.models.CountryInfo
import co.omise.android.models.PaymentMethod
import co.omise.android.models.Source
import co.omise.android.models.Token
import co.omise.android.models.backendType
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.annotations.TestOnly
import java.io.IOError
import java.util.Locale

/**
 * CreditCardActivity is the UI class for taking credit card information input from the user.
 */
class CreditCardActivity : OmiseActivity() {
    private lateinit var pKey: String
    private lateinit var cardHolderData: CardHolderDataList
    private lateinit var client: Client
    private lateinit var binding: ActivityCreditCardBinding

    /**
     * Target countries that supports AVS or the Address Verification System.
     * @see [link](https://www.omise.co/How-to-improve-my-authorization-rate-for-US-UK-and-Canadian-cardholders)
     */
    private val avsCountries = CountryInfo.ALL.filter { listOf("US", "GB", "CA").contains(it.code) }

    private val editTexts: Map<OmiseEditText, TextView> by lazy {
        mapOf(
            binding.editCardNumber to binding.textCardNumberError,
            binding.editCardName to binding.textCardNameError,
            binding.editExpiryDate to binding.textExpiryDateError,
            binding.editSecurityCode to binding.textSecurityCodeError,
            binding.editCountry to binding.textCountryError,
            binding.editStreet1 to binding.textStreet1Error,
            binding.editCity to binding.textCityError,
            binding.editState to binding.textStateError,
            binding.editPostalCode to binding.textPostalCodeError,
            binding.editEmail to binding.textEmailError,
            binding.editPhoneNumber to binding.textPhoneNumberError,
        )
    }

    private val billingAddressEditTexts: Map<OmiseEditText, TextView> by lazy {
        mapOf(
            binding.editCountry to binding.textCountryError,
            binding.editStreet1 to binding.textStreet1Error,
            binding.editCity to binding.textCityError,
            binding.editState to binding.textStateError,
            binding.editPostalCode to binding.textPostalCodeError,
        )
    }

    private var selectedCountry: CountryInfo? = null
        set(value) {
            field = value
            value?.let {
                invalidateBillingAddressForm()
            }
        }

    private fun isEmailValid(emailEdit: OmiseEditText): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailEdit.text!!).matches()
    }

    private fun isPhoneNumberValid(phoneNumberEdit: OmiseEditText): Boolean {
        return android.util.Patterns.PHONE.matcher(phoneNumberEdit.text!!).matches()
    }

    private fun updateEmailErrorText(hasFocus: Boolean) {
        // Clear error when field has focus (consistent with other fields)
        if (hasFocus) {
            with(binding.textEmailError) {
                text = ""
                visibility = GONE
            }
            return
        }

        // When field loses focus, validate only if not empty
        if (binding.editEmail.text?.isEmpty() == true || isEmailValid(binding.editEmail)) {
            with(binding.textEmailError) {
                text = ""
                visibility = GONE
            }
            return
        }

        binding.textEmailError.visibility = VISIBLE
        binding.textEmailError.text = getString(R.string.error_invalid_email)
    }

    private fun updatePhoneErrorText(hasFocus: Boolean) {
        // Clear error when field has focus (consistent with other fields)
        if (hasFocus) {
            with(binding.textPhoneNumberError) {
                text = ""
                visibility = GONE
            }
            return
        }

        // When field loses focus, validate only if not empty
        if (binding.editPhoneNumber.text?.isEmpty() == true || isPhoneNumberValid(binding.editPhoneNumber)) {
            with(binding.textPhoneNumberError) {
                text = ""
                visibility = GONE
            }
            return
        }

        binding.textPhoneNumberError.visibility = VISIBLE
        binding.textPhoneNumberError.text = getString(R.string.error_invalid_phone_number)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.getBooleanExtra(EXTRA_IS_SECURE, true)) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }

        binding = ActivityCreditCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        require(intent.hasExtra(EXTRA_PKEY)) { "Could not find ${::EXTRA_PKEY.name}." }
        pKey = requireNotNull(intent.getStringExtra(EXTRA_PKEY)) { "${::EXTRA_PKEY.name} must not be null." }
        cardHolderData = intent.parcelable<CardHolderDataList>(EXTRA_CARD_HOLDER_DATA) ?: CardHolderDataList(arrayListOf())

        if (!this::client.isInitialized) {
            client = Client(pKey)
        }
        val onBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    setResult(RESULT_CANCELED)
                    finish()
                }
            }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        initialize()
    }

    private fun EditText.getErrorMessage(): String? {
        return when (this) {
            binding.editCardNumber -> getString(R.string.error_invalid_card_number)
            binding.editCardName -> getString(R.string.error_invalid_card_name)
            binding.editExpiryDate -> getString(R.string.error_invalid_expiration_date)
            binding.editSecurityCode -> getString(R.string.error_invalid_security_code)
            binding.editStreet1 -> getString(R.string.error_required_street1)
            binding.editCity -> getString(R.string.error_required_city)
            binding.editState -> getString(R.string.error_required_state)
            binding.editPostalCode -> getString(R.string.error_required_postal_code)
            binding.editEmail -> getString(R.string.error_invalid_email)
            binding.editPhoneNumber -> getString(R.string.error_invalid_phone_number)
            else -> null
        }
    }

    private fun initialize() {
        setTitle(R.string.default_form_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.buttonSubmit.setOnClickListener(::submit)
        binding.buttonSecurityCodeTooltip.setOnClickListener(::showSecurityCodeTooltipDialog)
        binding.editCountry.setOnClickListener(::showCountryDropdownDialog)
        cardHolderDataVisibility()

        editTexts.forEach { (editText, errorText) ->
            editText.setOnFocusChangeListener { _, hasFocus ->
                // Handle card holder data fields (email and phone) with their special logic
                if (editText == binding.editEmail && cardHolderData.fields.contains(CardHolderDataField.EMAIL)) {
                    updateEmailErrorText(hasFocus)
                } else if (editText == binding.editPhoneNumber && cardHolderData.fields.contains(CardHolderDataField.PHONE_NUMBER)) {
                    updatePhoneErrorText(hasFocus)
                } else if (editText != binding.editEmail && editText != binding.editPhoneNumber) {
                    // Handle regular fields - skip email and phone as they have special handling
                    if (!hasFocus) {
                        try {
                            editText.validate()
                            errorText.text = null
                            errorText.visibility = GONE
                        } catch (e: InputValidationException.InvalidInputException) {
                            errorText.text = editText.getErrorMessage()
                            errorText.visibility = VISIBLE
                        } catch (e: InputValidationException.EmptyInputException) {
                            if (isBillingAddressRequired() && billingAddressEditTexts.containsKey(editText)) {
                                errorText.text = editText.getErrorMessage()
                                errorText.visibility = VISIBLE
                            } else {
                                errorText.text = null
                                errorText.visibility = GONE
                            }
                        }
                    } else {
                        // Clear error text when gaining focus
                        errorText.text = null
                        errorText.visibility = GONE
                    }
                }
                updateSubmitButton()
            }
            editText.setOnAfterTextChangeListener(::updateSubmitButton)
        }

        invalidateBillingAddressForm()

        getCapability()
    }

    @TestOnly
    fun setClient(client: Client) {
        this.client = client
    }

    private fun getCapability() {
        val getCapabilityRequest = Capability.GetCapabilitiesRequestBuilder().build()
        client.send(
            getCapabilityRequest,
            object : RequestListener<Capability> {
                override fun onRequestSucceed(model: Capability) {
                    val countryCode = model.country ?: Locale.getDefault().country
                    selectedCountry = CountryInfo.ALL.find { it.code == countryCode }
                }

                override fun onRequestFailed(throwable: Throwable) {
                    Snackbar.make(binding.scrollview, throwable.message.toString(), Snackbar.LENGTH_LONG).show()
                }
            },
        )
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

    private fun disableForm() {
        setFormEnabled(false)
    }

    private fun enableForm() {
        setFormEnabled(true)
    }

    private fun setFormEnabled(enabled: Boolean) {
        editTexts.forEach { (editText, _) -> editText.isEnabled = enabled }
        binding.buttonSubmit.isEnabled = enabled
    }

    private fun handleRequestFailed(throwable: Throwable) {
        enableForm()

        val message =
            when (throwable) {
                is IOError -> getString(R.string.error_io, throwable.message)
                is APIError -> throwable.getMessageFromResources(resources)
                else -> getString(R.string.error_unknown, throwable.message)
            }

        Snackbar.make(binding.scrollview, message, Snackbar.LENGTH_LONG).show()
    }

    private fun submit() {
        disableForm()

        val cardParam =
            CardParam(
                name = binding.editCardName.cardName,
                number = binding.editCardNumber.cardNumber,
                expirationMonth = binding.editExpiryDate.expiryMonth,
                expirationYear = binding.editExpiryDate.expiryYear,
                securityCode = binding.editSecurityCode.securityCode,
                country = selectedCountry?.code,
                street1 = binding.editStreet1.textOrNull?.toString(),
                city = binding.editCity.textOrNull?.toString(),
                state = binding.editState.textOrNull?.toString(),
                postalCode = binding.editPostalCode.textOrNull?.toString(),
                email = binding.editEmail.textOrNull?.toString(),
                phoneNumber = binding.editPhoneNumber.textOrNull?.toString(),
            )

        val request = Token.CreateTokenRequestBuilder(cardParam).build()
        val installmentTerms = intent.getIntExtra(EXTRA_SELECTED_INSTALLMENTS_TERM, 0)
        // check if the user is coming from a WLB installment
        var sourceRequest: Request<Source>? = null
        if (installmentTerms != 0) {
            // create a source request
            val amount = requireNotNull(intent.getLongExtra(EXTRA_AMOUNT, 0)) { "${::EXTRA_AMOUNT.name} must not be null." }

            val currency = requireNotNull(intent.getStringExtra(EXTRA_CURRENCY)) { "${::EXTRA_CURRENCY.name} must not be null." }
            val paymentMethod =
                requireNotNull(
                    intent.parcelable<PaymentMethod>(EXTRA_SELECTED_INSTALLMENTS_PAYMENT_METHOD),
                ) {
                    "${::EXTRA_SELECTED_INSTALLMENTS_PAYMENT_METHOD.name} must not be null."
                }
            val sourceType = (paymentMethod.backendType as? BackendType.Source)?.sourceType ?: return
            val capability =
                requireNotNull(
                    intent.parcelable<Capability>(EXTRA_CAPABILITY),
                ) { "${::EXTRA_CAPABILITY.name} must not be null." }
            sourceRequest =
                Source.CreateSourceRequestBuilder(amount, currency, sourceType)
                    .installmentTerm(installmentTerms)
                    .zeroInterestInstallments(capability.zeroInterestInstallments)
                    .build()
        }
        client.send(
            request,
            object : RequestListener<Token> {
                override fun onRequestSucceed(model: Token) {
                    val data =
                        Intent().apply {
                            putExtra(EXTRA_TOKEN, model.id)
                            putExtra(EXTRA_TOKEN_OBJECT, model)
                            putExtra(EXTRA_CARD_OBJECT, model.card)
                        }
                    if (sourceRequest == null) {
                        setResult(Activity.RESULT_OK, data)
                        finish()
                    } else {
                        // create source request
                        client.send(
                            sourceRequest,
                            object : RequestListener<Source> {
                                override fun onRequestSucceed(model: Source) {
                                    data.apply {
                                        putExtra(EXTRA_SOURCE_OBJECT, model)
                                    }
                                    setResult(Activity.RESULT_OK, data)
                                    finish()
                                }

                                override fun onRequestFailed(throwable: Throwable) {
                                    handleRequestFailed(throwable)
                                }
                            },
                        )
                    }
                }

                override fun onRequestFailed(throwable: Throwable) {
                    handleRequestFailed(throwable)
                }
            },
        )
    }

    private fun updateSubmitButton() {
        val validationResults = mutableMapOf<OmiseEditText, Boolean>()

        editTexts.keys.forEach { editText ->
            when {
                // Required fields (card info)
                editText in listOf(binding.editCardNumber, binding.editCardName, binding.editExpiryDate, binding.editSecurityCode) -> {
                    validationResults[editText] = editText.isValid
                }
                // Billing address fields (only required for AVS countries)
                editText in billingAddressEditTexts -> {
                    if (isBillingAddressRequired()) {
                        validationResults[editText] = editText.isValid
                    }
                    // If billing address not required, these fields don't affect validation
                }
                // Email field (required when requested by merchant)
                editText == binding.editEmail && cardHolderData.fields.contains(CardHolderDataField.EMAIL) -> {
                    validationResults[editText] = isEmailValid(binding.editEmail)
                }
                // Phone number field (required when requested by merchant)
                editText == binding.editPhoneNumber && cardHolderData.fields.contains(CardHolderDataField.PHONE_NUMBER) -> {
                    validationResults[editText] = isPhoneNumberValid(binding.editPhoneNumber)
                }
                // Other fields that are not visible/required don't affect validation
            }
        }

        // All included fields must be valid
        val isFormValid = validationResults.values.all { it }
        binding.buttonSubmit.isEnabled = isFormValid
    }

    private fun showSecurityCodeTooltipDialog() {
        val brand = CardNumber.brand(binding.editCardNumber.cardNumber)
        val dialog = SecurityCodeTooltipDialogFragment.newInstant(brand)
        dialog.show(supportFragmentManager, null)
    }

    private fun showCountryDropdownDialog() {
        val dialog = CountryListDialogFragment()
        dialog.listener =
            object : CountryListDialogFragment.CountryListDialogListener {
                override fun onCountrySelected(country: CountryInfo) {
                    selectedCountry = country
                }
            }
        dialog.show(supportFragmentManager, null)
    }

    private fun invalidateBillingAddressForm() {
        binding.editCountry.setText(selectedCountry?.name)
        binding.billingAddressContainer.visibility = if (isBillingAddressRequired()) View.VISIBLE else View.GONE
        billingAddressEditTexts.forEach { (editText, errorText) ->
            if (editText != binding.editCountry) {
                editText.text = null
                errorText.text = null
            }
        }
        updateSubmitButton()
    }

    private fun cardHolderDataVisibility() {
        if (cardHolderData.fields.contains(CardHolderDataField.EMAIL)) {
            binding.editEmail.visibility = View.VISIBLE
            binding.textEmailTitle.visibility = View.VISIBLE
        } else {
            binding.editEmail.visibility = View.GONE
            binding.textEmailTitle.visibility = View.GONE
            binding.textEmailError.visibility = View.GONE
        }

        if (cardHolderData.fields.contains(CardHolderDataField.PHONE_NUMBER)) {
            binding.editPhoneNumber.visibility = View.VISIBLE
            binding.textPhoneNumberTitle.visibility = View.VISIBLE
        } else {
            binding.editPhoneNumber.visibility = View.GONE
            binding.textPhoneNumberTitle.visibility = View.GONE
            binding.textPhoneNumberError.visibility = View.GONE
        }
    }

    private fun isBillingAddressRequired(): Boolean {
        return selectedCountry != null && avsCountries.contains(selectedCountry)
    }
}
