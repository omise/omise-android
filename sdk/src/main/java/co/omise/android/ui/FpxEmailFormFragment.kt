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
import kotlinx.android.synthetic.main.fragment_fpx_email_form.*

/**
 * FpxEmailFormFragment is the UI class to show an email form for FPX payments.
 */
class FpxEmailFormFragment : OmiseFragment() {

    var navigation: PaymentCreatorNavigation? = null
    var requester: PaymentCreatorRequester<Source>? = null

    private val emailEdit: OmiseEditText by lazy { edit_mail }
    private val emailErrorText by lazy { text_email_error }
    private val submitButton: Button by lazy { button_submit }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fpx_email_form, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title = getString(R.string.payment_method_fpx_title)
        setHasOptionsMenu(true)

        with(emailEdit) {
            setOnFocusChangeListener(::updateErrorText)
            setOnAfterTextChangeListener(::updateSubmitButton)
        }

        submitButton.setOnClickListener(::submitForm)
    }

    private fun updateErrorText(view: View, hasFocus: Boolean) {
        if (hasFocus || emailEdit.isValid) {
            with(emailErrorText) {
                text = ""
                visibility = INVISIBLE
            }
            return
        }

        emailErrorText.text = getString(R.string.error_invalid_email)
    }

    private fun updateSubmitButton() {
        submitButton.isEnabled = emailEdit.isValid
    }

    private fun submitForm() {
        val requester = requester ?: return
        val email = emailEdit.text?.toString()?.trim().orEmpty()
        val banks = requester.capability.paymentMethods?.find { it.name.equals("fpx") }?.banks ?: return

        navigation?.navigateToFpxBankChooser(banks, email)
    }
}
