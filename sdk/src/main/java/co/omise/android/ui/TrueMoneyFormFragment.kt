package co.omise.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.Button
import co.omise.android.R
import co.omise.android.extensions.setOnAfterTextChangeListener
import co.omise.android.extensions.setOnClickListener
import co.omise.android.models.Source
import co.omise.android.models.SourceType
import co.omise.android.databinding.FragmentTrueMoneyFormBinding

/**
 * TrueMoneyFormFragment is the UI class for handling TrueMoney payment method.
 */
class TrueMoneyFormFragment : OmiseFragment() {
    var requester: PaymentCreatorRequester<Source>? = null

    private var _binding: FragmentTrueMoneyFormBinding? = null
    private val binding get() = _binding!!

    private val phoneNumberEdit: OmiseEditText get() = binding.editPhoneNumber
    private val phoneNumberErrorText get() = binding.textPhoneNumberError
    private val submitButton: Button get() = binding.buttonSubmit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTrueMoneyFormBinding.inflate(inflater, container, false)
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

        title = getString(R.string.payment_truemoney_title)
        setHasOptionsMenu(true)

        with(phoneNumberEdit) {
            setOnFocusChangeListener { _, hasFocus ->
                updateErrorText(hasFocus)
            }
            setOnAfterTextChangeListener(::updateSubmitButton)
        }

        submitButton.setOnClickListener(::submitForm)
    }

    private fun updateErrorText(hasFocus: Boolean) {
        if (hasFocus || phoneNumberEdit.isValid) {
            with(phoneNumberErrorText) {
                text = ""
                visibility = INVISIBLE
            }
            return
        }

        phoneNumberErrorText.text = getString(R.string.error_invalid_phone_number)
    }

    private fun updateSubmitButton() {
        submitButton.isEnabled = phoneNumberEdit.isValid
    }

    private fun submitForm() {
        val requester = requester ?: return

        val phoneNumber = phoneNumberEdit.text?.toString()?.trim().orEmpty()

        val request =
            Source.CreateSourceRequestBuilder(
                requester.amount,
                requester.currency,
                SourceType.TrueMoney,
            )
                .phoneNumber(phoneNumber)
                .build()

        view?.let { setAllViewsEnabled(it, false) }
        requester.request(request) {
            view?.let { setAllViewsEnabled(it, true) }
        }
    }
}
