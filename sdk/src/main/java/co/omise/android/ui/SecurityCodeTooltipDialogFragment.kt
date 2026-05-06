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
import co.omise.android.databinding.DialogSecurityCodeTooltipBinding
import co.omise.android.extensions.getParcelableCompat
import co.omise.android.models.CardBrand

/**
 * SecurityCodeTooltipDialogFragment is a UI class to show the user information about
 * the security code and where it is found on the card.
 */
class SecurityCodeTooltipDialogFragment : DialogFragment() {
    private var _binding: DialogSecurityCodeTooltipBinding? = null
    private val binding get() = _binding!!

    private val cvvImage: ImageView get() = binding.cvvImage
    private val cvvDescriptionText: TextView get() = binding.cvvDescriptionText
    private val closeButton: ImageButton get() = binding.closeButton
    private var cardBrand: CardBrand? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardBrand = arguments?.getParcelableCompat(EXTRA_CARD_BRAND)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogSecurityCodeTooltipBinding.inflate(inflater, container, false)
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
