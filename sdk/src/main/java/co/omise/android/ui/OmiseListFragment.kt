package co.omise.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.omise.android.R
import kotlinx.android.synthetic.main.fragment_list.recycler_view


abstract class OmiseListFragment<T : OmiseListItem> : Fragment() {
    abstract fun onListItemClicked(option: T)

    private val recyclerView: RecyclerView by lazy { recycler_view }

    private val onClickListener = object : OmiseListItemClickListener {
        override fun onClick(item: OmiseListItem) {
            onListItemClicked(item as T)
        }
    }

    private val items: List<T> by lazy { arguments?.getParcelableArray(EXTRA_LIST_ITEMS)?.toList() as List<T> }
    private val adapter: OmiseListAdapter by lazy { OmiseListAdapter(items, onClickListener) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

//        val itemDecoration = PaymentOptionItemDecoration(context!!, layoutManager.orientation)
//        recyclerView.addItemDecoration(itemDecoration)

        recyclerView.adapter = adapter
    }

    companion object {
        const val EXTRA_LIST_ITEMS = "OmiseListFragment.items"
    }
}

class OmiseListAdapter(val list: List<OmiseListItem>, val listener: OmiseListItemClickListener?) : RecyclerView.Adapter<OmiseItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OmiseItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return OmiseItemViewHolder(itemView, listener)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: OmiseItemViewHolder, position: Int) {
        val option = list[position]
        holder.bind(option)
    }
}

class OmiseItemViewHolder(val view: View, val listener: OmiseListItemClickListener?) : RecyclerView.ViewHolder(view) {

    fun bind(item: OmiseListItem) {
        val optionImage = view.findViewById<ImageView>(R.id.image_item_icon)
        val nameText = view.findViewById<TextView>(R.id.text_item_title)
        val typeImage = view.findViewById<ImageView>(R.id.image_indicator_icon)

        optionImage.setImageResource(item.icon)
        nameText.setText(item.title)
        typeImage.setImageResource(item.indicatorIcon)

        view.setOnClickListener { listener?.onClick(item) }
    }
}

interface OmiseListItem {
    val icon: Int
    val title: Int
    val indicatorIcon: Int
}

interface OmiseListItemClickListener {
    fun onClick(item: OmiseListItem)
}
