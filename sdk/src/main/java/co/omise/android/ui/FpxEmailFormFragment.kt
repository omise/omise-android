package co.omise.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import co.omise.android.R
import co.omise.android.extensions.setOnAfterTextChangeListener
import co.omise.android.extensions.setOnClickListener
import co.omise.android.models.Bank
import co.omise.android.models.Source
import kotlinx.android.synthetic.main.fragment_fpx_email_form.*

/**
 * FpxEmailFormFragment is the UI class to show an email form for FPX payments.
 */
internal class FpxEmailFormFragment : OmiseFragment() {

    var navigation: PaymentCreatorNavigation? = null
    var requester: PaymentCreatorRequester<Source>? = null

    private val emailEdit: OmiseEditText by lazy { edit_email }
    private val submitButton: Button by lazy { button_submit }
    private val allowedEmailFormat = "\\A[\\w+\\-.]+@[a-z\\d\\-.]+\\.[a-z]{2,}\\z"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fpx_email_form, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title = getString(R.string.payment_method_fpx_title)
        setHasOptionsMenu(true)

        with(emailEdit) {
            setOnAfterTextChangeListener(::updateSubmitButton)
        }

        submitButton.setOnClickListener(::submitForm)
    }

    private fun updateSubmitButton() {
        val text = emailEdit.text.toString()
        when {
            text.isEmpty() || allowedEmailFormat.toRegex().matches(text) -> submitButton.isEnabled = true
            else -> submitButton.isEnabled = false
        }
    }

    private fun submitForm() {
        val requester = requester ?: return
        val paymentMethod = requester.capability?.paymentMethods?.find { it.name.equals("fpx") }
        val banks = if(requester.specificPaymentMode) mockBanks() else paymentMethod?.banks
        val email = emailEdit.text?.toString()?.trim().orEmpty()

        navigation?.navigateToFpxBankChooser(banks, email)
    }

    private fun mockBanks() : List<Bank> {
        return listOf(
                Bank("AmBank", "ambank", true),
                Bank("OCBC Bank", "ocbc", false)
        )
    }
}
