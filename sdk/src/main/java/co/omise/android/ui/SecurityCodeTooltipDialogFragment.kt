package co.omise.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import co.omise.android.R
import co.omise.android.models.CardBrand
import kotlinx.android.synthetic.main.dialog_security_code_tooltip.close_button
import kotlinx.android.synthetic.main.dialog_security_code_tooltip.cvv_description_text
import kotlinx.android.synthetic.main.dialog_security_code_tooltip.cvv_image


class SecurityCodeTooltipDialogFragment : DialogFragment() {

    private val cvvImage: ImageView by lazy { cvv_image }
    private val cvvDescriptionText: TextView by lazy { cvv_description_text }
    private val closeButton: ImageButton by lazy { close_button }
    private lateinit var cardBrand: CardBrand

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cardBrand = arguments?.getParcelable(EXTRA_CARD_BRAND)
                ?: throw IllegalAccessException("Can not found ${::EXTRA_CARD_BRAND.name}.")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_security_code_tooltip, container)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        closeButton.setOnClickListener{ dismiss() }

        when (cardBrand) {
            CardBrand.AMEX -> {
                cvvImage.setImageResource(R.drawable.cvv_4_digits)
                cvvDescriptionText.setText(R.string.cvv_tooltip_4_digits)
            }
            else -> {
                cvvImage.setImageResource(R.drawable.cvv_3_digits)
                cvvDescriptionText.setText(R.string.cvv_tooltip_3_digits)
            }
        }
    }

    companion object {
        const val EXTRA_CARD_BRAND = "SecurityCodeTooltipDialogFragment.CardBrand"

        fun newInstant(brand: CardBrand): SecurityCodeTooltipDialogFragment {
            val argument = Bundle()
            argument.putParcelable(EXTRA_CARD_BRAND, brand)

            val dialogFragment = SecurityCodeTooltipDialogFragment()
            dialogFragment.arguments = argument

            return dialogFragment
        }
    }
}