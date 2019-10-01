package co.omise.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import co.omise.android.R
import co.omise.android.extensions.setOnAfterTextChangeListener
import co.omise.android.extensions.setOnClickListener
import co.omise.android.models.SupportedEContext
import co.omise.android.models.Source
import co.omise.android.models.SourceType
import kotlinx.android.synthetic.main.fragment_econtext_form.button_submit
import kotlinx.android.synthetic.main.fragment_econtext_form.edit_email
import kotlinx.android.synthetic.main.fragment_econtext_form.edit_full_name
import kotlinx.android.synthetic.main.fragment_econtext_form.edit_phone_number
import kotlinx.android.synthetic.main.fragment_econtext_form.text_email_error
import kotlinx.android.synthetic.main.fragment_econtext_form.text_full_name_error
import kotlinx.android.synthetic.main.fragment_econtext_form.text_phone_number_error


class EContextFormFragment : OmiseFragment() {

    var requester: PaymentCreatorRequester<Source>? = null

    private val type: SupportedEContext? by lazy {
        arguments?.getParcelable<SupportedEContext>(EXTRA_ECONTEXT_TYPE)
    }
    private val fullNameEdit: OmiseEditText by lazy { edit_full_name }
    private val emailEdit: OmiseEditText by lazy { edit_email }
    private val phoneNumberEdit: OmiseEditText by lazy { edit_phone_number }
    private val fullNameErrorText by lazy { text_full_name_error }
    private val emailErrorText by lazy { text_email_error }
    private val phoneNumberErrorText by lazy { text_phone_number_error }
    private val submitButton: Button by lazy { button_submit }
    private val formInputWithErrorTexts: List<Pair<OmiseEditText, TextView>> by lazy {
        listOf(
                Pair(fullNameEdit, fullNameErrorText),
                Pair(emailEdit, emailErrorText),
                Pair(phoneNumberEdit, phoneNumberErrorText)
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_econtext_form, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title = when (type) {
            SupportedEContext.ConvenienceStore -> getString(R.string.title_convenience_store)
            SupportedEContext.PayEasy -> getString(R.string.title_pay_easy)
            SupportedEContext.Netbanking -> getString(R.string.title_netbanking)
            null -> getString(R.string.econtext_title)
        }
        setHasOptionsMenu(true)

        formInputWithErrorTexts.forEach {
            it.first.setOnFocusChangeListener(::updateErrorText)
            it.first.setOnAfterTextChangeListener(::updateSubmitButton)
        }

        submitButton.setOnClickListener(::submitForm)
    }

    private fun updateErrorText(view: View, hasFocus: Boolean) {
        val editText = view as OmiseEditText
        val errorText = formInputWithErrorTexts.first { it.first == editText }.second

        if (hasFocus || editText.isValid) {
            errorText.text = ""
            errorText.visibility = INVISIBLE
            return
        }

        errorText.text = when (editText) {
            fullNameEdit -> getString(R.string.error_invalid_full_name)
            emailEdit -> getString(R.string.error_invalid_email)
            phoneNumberEdit -> getString(R.string.error_invalid_phone_number)
            else -> getString(R.string.error_unknown_without_reason)
        }
        errorText.visibility = VISIBLE
    }

    private fun updateSubmitButton() {
        val isFormValid = formInputWithErrorTexts.map { it.first.isValid }
                .reduce { acc, b -> acc && b }
        submitButton.isEnabled = isFormValid
    }

    private fun submitForm() {
        val requester = requester ?: return

        val fullName = fullNameEdit.text?.toString()?.trim().orEmpty()
        val email = emailEdit.text?.toString()?.trim().orEmpty()
        val phoneNumber = phoneNumberEdit.text?.toString()?.trim().orEmpty()

        val request = Source.CreateSourceRequestBuilder(requester.amount, requester.currency, SourceType.Econtext)
                .name(fullName)
                .email(email)
                .phoneNumber(phoneNumber)
                .build()

        view?.let { setAllViewsEnabled(it, false) }
        requester.request(request) {
            view?.let { setAllViewsEnabled(it, true) }
        }
    }

    companion object {
        private const val EXTRA_ECONTEXT_TYPE = "EContextFormFragment.econtextType"
        fun newInstance(eContext: SupportedEContext): EContextFormFragment = EContextFormFragment().apply {
            arguments = Bundle().apply {
                putParcelable(EXTRA_ECONTEXT_TYPE, eContext)
            }
        }
    }
}
