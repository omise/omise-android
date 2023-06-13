package co.omise.android.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import co.omise.android.R
import co.omise.android.models.CountryInfo
import kotlinx.android.synthetic.main.dialog_country_list.close_button
import kotlinx.android.synthetic.main.dialog_country_list.country_list
import java.text.Collator

/**
 * CountryDropdownDialogFragment is a UI class to show the user
 * the security code and where it is found on the card.
 */
class CountryListDialogFragment : DialogFragment() {

    interface CountryListDialogListener {
        fun onCountrySelected(country: CountryInfo)
    }

    private val listView: RecyclerView by lazy { country_list }
    private val closeButton: ImageButton by lazy { close_button }
    private var selectedCountry: CountryInfo? = null

    var listener: CountryListDialogListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedCountry = arguments?.getParcelable(EXTRA_SELECTED_COUNTRY)
    }

    override fun getTheme(): Int {
        return R.style.OmiseDialogTheme
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_country_list, container)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        closeButton.setOnClickListener { dismiss() }

        val adapter = CountryListAdapter(::onCountryClick)
        listView.adapter = adapter
        adapter.submitList(CountryInfo.ALL.sortedWith { o1, o2 ->
            Collator.getInstance().compare(o1.displayName, o2.displayName)
        })
    }

    private fun onCountryClick(country: CountryInfo) {
        listener?.onCountrySelected(country)
        dismiss()
    }

    companion object {
        const val EXTRA_SELECTED_COUNTRY = "CountryDropdownDialogFragment.SelectedCountry"

        fun newInstant(selectedCountry: CountryInfo? = null): CountryListDialogFragment {
            val argument = Bundle()
            argument.putParcelable(EXTRA_SELECTED_COUNTRY, selectedCountry)

            val dialogFragment = CountryListDialogFragment()
            dialogFragment.arguments = argument

            return dialogFragment
        }
    }
}