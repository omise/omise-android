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
import co.omise.android.databinding.FragmentEcontextFormBinding
import co.omise.android.extensions.getParcelableCompat
import co.omise.android.extensions.setOnAfterTextChangeListener
import co.omise.android.extensions.setOnClickListener
import co.omise.android.models.Source
import co.omise.android.models.SourceType
import co.omise.android.models.SupportedEcontext

/**
 * EContextFormFragment is the UI class for handling all EContext payment methods.
 */
class EContextFormFragment : OmiseFragment() {
    var requester: PaymentCreatorRequester<Source>? = null

    private var _binding: FragmentEcontextFormBinding? = null
    private val binding get() = _binding!!

    private val type: SupportedEcontext? by lazy {
        arguments?.getParcelableCompat<SupportedEcontext>(EXTRA_ECONTEXT_TYPE)
    }
    private val fullNameEdit: OmiseEditText get() = binding.editFullName
    private val emailEdit: OmiseEditText get() = binding.editEmail
    private val phoneNumberEdit: OmiseEditText get() = binding.editPhoneNumber
    private val fullNameErrorText: TextView get() = binding.textFullNameError
    private val emailErrorText: TextView get() = binding.textEmailError
    private val phoneNumberErrorText: TextView get() = binding.textPhoneNumberError
    private val submitButton: Button get() = binding.buttonSubmit
    private val formInputWithErrorTexts: List<Pair<OmiseEditText, TextView>> get() =
        listOf(
            Pair(fullNameEdit, fullNameErrorText),
            Pair(emailEdit, emailErrorText),
            Pair(phoneNumberEdit, phoneNumberErrorText),
        )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEcontextFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title = when (type) {
            SupportedEcontext.ConvenienceStore -> getString(R.string.title_convenience_store)
            SupportedEcontext.PayEasy -> getString(R.string.title_pay_easy)
            SupportedEcontext.Netbanking -> getString(R.string.title_netbank)
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
        submitButton.isEnabled = formInputWithErrorTexts.map { it.first.isValid }.reduce { acc, b -> acc && b }
    }

    private fun submitForm() {
        val requester = requester ?: return
        val fullName = fullNameEdit.text?.toString()?.trim().orEmpty()
        val email = emailEdit.text?.toString()?.trim().orEmpty()
        val phoneNumber = phoneNumberEdit.text?.toString()?.trim().orEmpty()
        val request = Source.CreateSourceRequestBuilder(requester.amount, requester.currency, SourceType.Econtext)
            .name(fullName).email(email).phoneNumber(phoneNumber).build()
        view?.let { setAllViewsEnabled(it, false) }
        requester.request(request) { view?.let { setAllViewsEnabled(it, true) } }
    }

    companion object {
        private const val EXTRA_ECONTEXT_TYPE = "EContextFormFragment.econtextType"
        fun newInstance(eContext: SupportedEcontext): EContextFormFragment =
            EContextFormFragment().apply {
                arguments = Bundle().apply { putParcelable(EXTRA_ECONTEXT_TYPE, eContext) }
            }
    }
}
