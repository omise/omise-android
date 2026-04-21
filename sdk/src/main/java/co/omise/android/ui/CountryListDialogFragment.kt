package co.omise.android.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import co.omise.android.R
import co.omise.android.databinding.DialogCountryListBinding
import co.omise.android.models.CountryInfo
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

    private var _binding: DialogCountryListBinding? = null
    private val binding get() = _binding!!

    private val listView: RecyclerView get() = binding.countryList
    private val toolbar: Toolbar get() = binding.toolbarCountryList

    var listener: CountryListDialogListener? = null

    override fun getTheme(): Int {
        return R.style.OmiseFullScreenDialogTheme
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = DialogCountryListBinding.inflate(inflater, container, false)
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
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.close_menu -> dismiss()
            }
            true
        }

        val adapter = CountryListAdapter(::onCountryClick)
        listView.adapter = adapter
        adapter.submitList(
            CountryInfo.ALL.sortedWith { o1, o2 ->
                Collator.getInstance().compare(o1.name, o2.name)
            },
        )
    }

    private fun onCountryClick(country: CountryInfo) {
        listener?.onCountrySelected(country)
        dismiss()
    }
}
