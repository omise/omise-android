package co.omise.android.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import co.omise.android.R
import co.omise.android.extensions.getParcelableCompat
import co.omise.android.models.CardBrand
import kotlinx.android.synthetic.main.dialog_security_code_tooltip.close_button
import kotlinx.android.synthetic.main.dialog_security_code_tooltip.cvv_description_text
import kotlinx.android.synthetic.main.dialog_security_code_tooltip.cvv_image

/**
 * SecurityCodeTooltipDialogFragment is a UI class to show the user information about
 * the security code and where it is found on the card.
 */
class SecurityCodeTooltipDialogFragment : DialogFragment() {
    private val cvvImage: ImageView by lazy { cvv_image }
    private val cvvDescriptionText: TextView by lazy { cvv_description_text }
    private val closeButton: ImageButton by lazy { close_button }
    private var cardBrand: CardBrand? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cardBrand = arguments?.getParcelableCompat(EXTRA_CARD_BRAND)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.dialog_security_code_tooltip, container)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        closeButton.setOnClickListener { dismiss() }

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

        fun newInstant(brand: CardBrand? = null): SecurityCodeTooltipDialogFragment {
            val argument = Bundle()
            argument.putParcelable(EXTRA_CARD_BRAND, brand)

            val dialogFragment = SecurityCodeTooltipDialogFragment()
            dialogFragment.arguments = argument

            return dialogFragment
        }
    }
}
