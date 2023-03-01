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
import kotlinx.android.synthetic.main.fragment_true_money_form.*

/**
 * TrueMoneyFormFragment is the UI class for handling TrueMoney payment method.
 */
class TrueMoneyFormFragment : OmiseFragment() {
    var requester: PaymentCreatorRequester<Source>? = null

    private val phoneNumberEdit: OmiseEditText by lazy { edit_phone_number }
    private val phoneNumberErrorText by lazy { text_phone_number_error }
    private val submitButton: Button by lazy { button_submit }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_true_money_form, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

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

        val request = Source.CreateSourceRequestBuilder(
            requester.amount,
            requester.currency,
            SourceType.TrueMoney
        )
            .phoneNumber(phoneNumber)
            .build()

        view?.let { setAllViewsEnabled(it, false) }
        requester.request(request) {
            view?.let { setAllViewsEnabled(it, true) }
        }
    }
}
