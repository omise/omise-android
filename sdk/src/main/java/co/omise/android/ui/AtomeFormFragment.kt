package co.omise.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import co.omise.android.R
import co.omise.android.databinding.FragmentAtomeFormBinding
import co.omise.android.extensions.setOnAfterTextChangeListener
import co.omise.android.extensions.setOnClickListener
import co.omise.android.models.Billing
import co.omise.android.models.Item
import co.omise.android.models.Shipping
import co.omise.android.models.Source
import co.omise.android.models.SourceType

/**
 * AtomeFormFragment is the UI class for handling all Atome payment methods.
 */
class AtomeFormFragment : OmiseFragment() {
    var requester: PaymentCreatorRequester<Source>? = null

    private var _binding: FragmentAtomeFormBinding? = null
    private val binding get() = _binding!!

    private val fullNameEdit: OmiseEditText get() = binding.editFullName
    private val emailEdit: OmiseEditText get() = binding.editEmail
    private val emailErrorText get() = binding.textAtomeEmailError
    private val phoneNumberEdit: OmiseEditText get() = binding.editPhoneNumber
    private val phoneNumberErrorText get() = binding.textPhoneNumberError

    private val shippingStreetEdit get() = binding.editShippingStreet
    private val shippingPostalEdit get() = binding.editShippingPostal
    private val shippingCityEdit get() = binding.editShippingCity
    private val shippingCountryEdit get() = binding.editShippingCountry
    private val shippingAddressErrorText get() = binding.textShippingAddressError

    private val billingStreetEdit get() = binding.editBillingStreet
    private val billingPostalEdit get() = binding.editBillingPostal
    private val billingCityEdit get() = binding.editBillingCity
    private val billingCountryEdit get() = binding.editBillingCountry
    private val billingAddressErrorText get() = binding.textBillingAddressError

    private val checkBoxBillingShipping get() = binding.checkboxBillingShipping
    private val submitButton: Button get() = binding.buttonSubmit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAtomeFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        with(emailEdit) {
            setOnFocusChangeListener { _, hasFocus ->
                updateEmailErrorText(hasFocus)
            }
            setOnAfterTextChangeListener(::updateSubmitButton)
        }

        with(phoneNumberEdit) {
            setOnFocusChangeListener { _, hasFocus ->
                updatePhoneErrorText(hasFocus)
            }
            setOnAfterTextChangeListener(::updateSubmitButton)
        }

        with(shippingStreetEdit) {
            setOnFocusChangeListener { _, hasFocus ->
                updateShippingAddressErrorText(hasFocus)
            }
            setOnAfterTextChangeListener(::updateSubmitButton)
        }

        with(shippingPostalEdit) {
            setOnAfterTextChangeListener(::updateSubmitButton)
        }

        with(shippingCityEdit) {
            setOnAfterTextChangeListener(::updateSubmitButton)
        }

        with(shippingCountryEdit) {
            setOnFocusChangeListener { _, hasFocus ->
                updateShippingAddressErrorText(hasFocus)
            }
            setOnAfterTextChangeListener(::updateSubmitButton)
        }

        with(billingCountryEdit) {
            setOnFocusChangeListener { _, hasFocus ->
                updateBillingAddressErrorText(hasFocus)
            }
            setOnAfterTextChangeListener(::updateSubmitButton)
        }

        checkBoxBillingShipping.toggle()
        checkBoxBillingShipping.setOnClickListener(::onBillingShippingCheckboxClicked)

        submitButton.setOnClickListener(::submitForm)
    }

    private fun updateEmailErrorText(hasFocus: Boolean) {
        if (hasFocus || emailEdit.text?.isEmpty() == true || isEmailValid(emailEdit)) {
            with(emailErrorText) {
                text = ""
                visibility = GONE
            }
            return
        }

        emailErrorText.visibility = VISIBLE
        emailErrorText.text = getString(R.string.error_invalid_email)
    }

    private fun updatePhoneErrorText(hasFocus: Boolean) {
        if (hasFocus || isPhoneNumberValid(phoneNumberEdit)) {
            with(phoneNumberErrorText) {
                text = ""
                visibility = GONE
            }
            return
        }

        phoneNumberErrorText.visibility = VISIBLE
        phoneNumberErrorText.text = getString(R.string.error_invalid_phone_number)
    }

    private fun updateShippingAddressErrorText(hasFocus: Boolean) {
        if (hasFocus || (
                shippingStreetEdit.isValid &&
                    shippingPostalEdit.isValid &&
                    shippingCityEdit.isValid &&
                    isCountryCodeValid(shippingCountryEdit)
            )
        ) {
            with(shippingAddressErrorText) {
                text = ""
                visibility = GONE
            }
            return
        }

        shippingAddressErrorText.visibility = VISIBLE
        shippingAddressErrorText.text = getString(R.string.error_invalid_address)
    }

    private fun updateBillingAddressErrorText(hasFocus: Boolean) {
        if (hasFocus || billingCountryEdit.text?.isEmpty() == true || isCountryCodeValid(billingCountryEdit)) {
            with(billingAddressErrorText) {
                text = ""
                visibility = GONE
            }
            return
        }

        billingAddressErrorText.visibility = VISIBLE
        billingAddressErrorText.text = getString(R.string.error_invalid_address)
    }

    private fun isEmailValid(emailEdit: OmiseEditText): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailEdit.text!!).matches()
    }

    private fun isPhoneNumberValid(phoneNumberEdit: OmiseEditText): Boolean {
        return android.util.Patterns.PHONE.matcher(phoneNumberEdit.text!!).matches()
    }

    private fun isCountryCodeValid(countryCodeEdit: OmiseEditText): Boolean {
        return countryCodeEdit.length() == 2
    }

    private fun onBillingShippingCheckboxClicked(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked

            when (view.id) {
                R.id.checkbox_billing_shipping -> {
                    if (checked) {
                        binding.billingAddress.visibility = GONE
                    } else {
                        binding.billingAddress.visibility = VISIBLE
                    }
                }
            }
        }
    }

    private fun updateSubmitButton() {
        submitButton.isEnabled = (emailEdit.text?.isEmpty() == true || isEmailValid(emailEdit)) &&
            isPhoneNumberValid(phoneNumberEdit) &&
            shippingStreetEdit.isValid &&
            shippingPostalEdit.isValid &&
            shippingCityEdit.isValid &&
            isCountryCodeValid(shippingCountryEdit) &&
            (billingCityEdit.text?.isEmpty() == true || isCountryCodeValid(billingCountryEdit))
    }

    private fun submitForm() {
        val requester = requester ?: return
        val requestBuilder = Source.CreateSourceRequestBuilder(requester.amount, requester.currency, SourceType.Atome)

        val fullName = fullNameEdit.text?.toString()?.trim().orEmpty()
        requestBuilder.name(fullName)

        val email = emailEdit.text?.toString()?.trim().orEmpty()
        requestBuilder.email(email)

        val phoneNumber = phoneNumberEdit.text?.toString()?.trim().orEmpty()
        requestBuilder.phoneNumber(phoneNumber)

        val shippingStreet = shippingStreetEdit.text?.toString()?.trim().orEmpty()
        val shippingPostal = shippingPostalEdit.text?.toString()?.trim().orEmpty()
        val shippingCity = shippingCityEdit.text?.toString()?.trim().orEmpty()
        val shippingCountry = shippingCountryEdit.text?.toString()?.trim().orEmpty()
        requestBuilder.shipping(
            Shipping(
                street1 = shippingStreet,
                postalCode = shippingPostal,
                city = shippingCity,
                country = shippingCountry,
            ),
        ).items(
            listOf(
                Item(
                    "3427842",
                    "Shoes",
                    "Prada shoes",
                    "1",
                    requester.amount.toString(),
                    "www.kan.com/product/shoes",
                    "www.kan.com/product/shoes/image",
                    "Gucci",
                ),
            ),
        )

        if (!checkBoxBillingShipping.isChecked) {
            requestBuilder.billing(
                Billing(
                    street1 = billingStreetEdit.text?.toString()?.trim().orEmpty(),
                    postalCode = billingPostalEdit.text?.toString()?.trim().orEmpty(),
                    city = billingCityEdit.text?.toString()?.trim().orEmpty(),
                    country = billingCountryEdit.text?.toString()?.trim().orEmpty(),
                ),
            )
        } else {
            requestBuilder.billing(
                Billing(
                    street1 = shippingStreet,
                    postalCode = shippingPostal,
                    city = shippingCity,
                    country = shippingCountry,
                ),
            )
        }

        val request = requestBuilder.build()
        view?.let { setAllViewsEnabled(it, false) }
        requester.request(request) {
            view?.let { setAllViewsEnabled(it, true) }
        }
    }
}
