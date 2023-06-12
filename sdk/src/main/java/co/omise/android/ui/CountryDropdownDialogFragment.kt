package co.omise.android.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import co.omise.android.R
import co.omise.android.models.CardBrand
import co.omise.android.models.CountryInfo

/**
 * CountryDropdownDialogFragment is a UI class to show the user
 * the security code and where it is found on the card.
 */
class CountryDropdownDialogFragment : DialogFragment() {

//    private val cvvImage: ImageView by lazy { cvv_image }
//    private val cvvDescriptionText: TextView by lazy { cvv_description_text }
//    private val closeButton: ImageButton by lazy { close_button }
    private var selectedCountry: CountryInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedCountry = arguments?.getParcelable(EXTRA_SELECTED_COUNTRY)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_security_code_tooltip, container)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

//        closeButton.setOnClickListener { dismiss() }
    }

    companion object {
        const val EXTRA_SELECTED_COUNTRY = "CountryDropdownDialogFragment.SelectedCountry"

        fun newInstant(selectedCountry: CountryInfo? = null): CountryDropdownDialogFragment {
            val argument = Bundle()
            argument.putParcelable(EXTRA_SELECTED_COUNTRY, selectedCountry)

            val dialogFragment = CountryDropdownDialogFragment()
            dialogFragment.arguments = argument

            return dialogFragment
        }
    }
}