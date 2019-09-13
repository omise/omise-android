package co.omise.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import co.omise.android.R
import co.omise.android.extensions.setOnClickListener
import co.omise.android.models.Source
import kotlinx.android.synthetic.main.fragment_econtext_form.button_submit
import kotlinx.android.synthetic.main.fragment_econtext_form.edit_email
import kotlinx.android.synthetic.main.fragment_econtext_form.edit_full_name
import kotlinx.android.synthetic.main.fragment_econtext_form.edit_phone


class EContextFormFragment : OmiseFragment() {

    var requester: PaymentCreatorRequester<Source>? = null

    private val fullNameEdit: OmiseEditText by lazy { edit_full_name }
    private val emailEdit: OmiseEditText by lazy { edit_email }
    private val phoneEdit: OmiseEditText by lazy { edit_phone }
    private val submitButton: Button by lazy { button_submit }
    private val formEdits: List<OmiseEditText> by lazy {
        listOf(fullNameEdit, emailEdit, phoneEdit)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_econtext_form, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title = getString(R.string.econtext_title)
        setHasOptionsMenu(true)

        submitButton.setOnClickListener(::submitForm)
    }

    private fun submitForm() {
        val fullName = fullNameEdit.text?.toString()?.trim() ?: ""
        val email = emailEdit.text?.toString()?.trim() ?: ""
        val phone = phoneEdit.text?.toString()?.trim() ?: ""
    }

    companion object {
        fun newInstance(): EContextFormFragment = EContextFormFragment()
    }
}
