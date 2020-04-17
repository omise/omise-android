package co.omise.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import co.omise.android.R
import co.omise.android.models.Source
import kotlinx.android.synthetic.main.fragment_true_money_form.*

/**
 * TrueMoneyFormFragment is the UI class for handling TrueMoney payment method.
 */
class TrueMoneyFormFragment : OmiseFragment() {
    var requester: PaymentCreatorRequester<Source>? = null

    private val phoneNumberEdit: OmiseEditText by lazy { edit_phone_number }
    private val phoneNumberErrorText by lazy { text_phone_number_error }
    private val submitButton: Button by lazy { button_submit }
    private val formInputWithErrorTexts: List<Pair<OmiseEditText, TextView>> by lazy {
        listOf(
                Pair(phoneNumberEdit, phoneNumberErrorText)
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_true_money_form, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title = getString(R.string.payment_truemoney_title)
        setHasOptionsMenu(true)
    }
}
