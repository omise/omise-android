package co.omise.android.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import co.omise.android.databinding.ListCountryItemBinding
import co.omise.android.models.CountryInfo

internal class CountryListAdapter(private val onClick: (CountryInfo) -> Unit) :
    ListAdapter<CountryInfo, CountryListAdapter.CountryViewHolder>(CountryDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CountryViewHolder {
        val binding = ListCountryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountryViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(
        holder: CountryViewHolder,
        position: Int,
    ) {
        val country = getItem(position)
        holder.bind(country)
    }

    class CountryViewHolder(val binding: ListCountryItemBinding, val onClick: (CountryInfo) -> Unit) : ViewHolder(binding.root) {
        private var currentCountry: CountryInfo? = null

        init {
            binding.root.setOnClickListener {
                currentCountry?.let {
                    onClick(it)
                }
            }
        }

        fun bind(country: CountryInfo) {
            currentCountry = country
            binding.textItemTitle.text = country.name
        }
    }
}

object CountryDiffCallback : DiffUtil.ItemCallback<CountryInfo>() {
    override fun areItemsTheSame(
        oldItem: CountryInfo,
        newItem: CountryInfo,
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: CountryInfo,
        newItem: CountryInfo,
    ): Boolean {
        return oldItem.name == newItem.name
    }
}
