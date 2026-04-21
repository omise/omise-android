package co.omise.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import co.omise.android.R
import co.omise.android.extensions.setOnAfterTextChangeListener
import co.omise.android.extensions.setOnClickListener
import co.omise.android.models.Source
import co.omise.android.databinding.FragmentFpxEmailFormBinding

/**
 * FpxEmailFormFragment is the UI class to show an email form for FPX payments.
 */
internal class FpxEmailFormFragment : OmiseFragment() {
    var navigation: PaymentCreatorNavigation? = null
    var requester: PaymentCreatorRequester<Source>? = null

    private var _binding: FragmentFpxEmailFormBinding? = null
    private val binding get() = _binding!!

    private val emailEdit: OmiseEditText get() = binding.editEmail
    private val submitButton: Button get() = binding.buttonSubmit
    private val allowedEmailFormat = "\\A[\\w+\\-.]+@[a-z\\d\\-.]+\\.[a-z]{2,}\\z"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFpxEmailFormBinding.inflate(inflater, container, false)
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

        title = getString(R.string.payment_method_fpx_title)
        setHasOptionsMenu(true)

        with(emailEdit) {
            setOnAfterTextChangeListener(::updateSubmitButton)
        }

        submitButton.setOnClickListener(::submitForm)
    }

    private fun updateSubmitButton() {
        val text = emailEdit.text.toString()

        submitButton.isEnabled = text.isEmpty() || allowedEmailFormat.toRegex().matches(text)
    }

    private fun submitForm() {
        val requester = requester ?: return
        val banks = requester.capability.paymentMethods?.find { it.name.equals("fpx") }?.banks
        val email = emailEdit.text?.toString()?.trim().orEmpty()

        navigation?.navigateToFpxBankChooser(banks, email)
    }
}
