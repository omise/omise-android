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
 * [CountryListDialogFragment] provides a dialog for selecting a country.
 */
class CountryListDialogFragment : DialogFragment() {
    /**
    * The interface to receive [CountryInfo] object after selecting a country.
    */
    interface CountryListDialogListener {
        fun onCountrySelected(country: CountryInfo)
    }

    private val listView: RecyclerView by lazy { country_list }
    private val closeButton: ImageButton by lazy { close_button }

    var listener: CountryListDialogListener? = null

    override fun getTheme(): Int {
        return R.style.OmiseFullScreenDialogTheme
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
            Collator.getInstance().compare(o1.name, o2.name)
        })
    }

    private fun onCountryClick(country: CountryInfo) {
        listener?.onCountrySelected(country)
        dismiss()
    }
}
