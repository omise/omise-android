package co.omise.android.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import co.omise.android.R
import co.omise.android.models.CountryInfo

internal class CountryListAdapter(private val onClick: (CountryInfo) -> Unit) :
    ListAdapter<CountryInfo, CountryListAdapter.CountryViewHolder>(CountryDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CountryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_country_item, parent, false)
        return CountryViewHolder(view, onClick)
    }

    override fun onBindViewHolder(
        holder: CountryViewHolder,
        position: Int,
    ) {
        val country = getItem(position)
        holder.bind(country)
    }

    class CountryViewHolder(itemView: View, val onClick: (CountryInfo) -> Unit) : ViewHolder(itemView) {
        private var currentCountry: CountryInfo? = null
        private val titleTextView: TextView = itemView.findViewById(R.id.text_item_title)

        init {
            itemView.setOnClickListener {
                currentCountry?.let {
                    onClick(it)
                }
            }
        }

        fun bind(country: CountryInfo) {
            currentCountry = country
            titleTextView.text = country.name
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