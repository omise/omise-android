package co.omise.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import co.omise.android.R
import kotlinx.android.synthetic.main.fragment_list.no_data_text
import kotlinx.android.synthetic.main.fragment_list.recycler_view

/**
 * OmiseListFragment is the base class for all list-based UI classes.
 */
abstract class OmiseListFragment<T : OmiseListItem> : OmiseFragment() {
    abstract fun onListItemClicked(item: T)

    abstract fun listItems(): List<T>

    protected val noDataText: TextView by lazy { no_data_text }
    private val recyclerView: RecyclerView by lazy { recycler_view }

    private val onClickListener =
        object : OmiseListItemClickListener {
            override fun onClick(item: OmiseListItem) {
                onListItemClicked(item as T)
            }
        }

    private val adapter: OmiseListAdapter by lazy { OmiseListAdapter(listItems(), onClickListener) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        recyclerView.layoutManager = layoutManager
        context?.let { recyclerView.addItemDecoration(OmiseItemDecoration(it)) }

        if (adapter.itemCount == 0) {
            noDataText.visibility = View.VISIBLE
        } else {
            noDataText.visibility = View.INVISIBLE
        }

        recyclerView.adapter = adapter
    }

    protected fun setUiEnabled(isEnabled: Boolean) {
        recyclerView.isEnabled = isEnabled
    }
}

class OmiseListAdapter(val list: List<OmiseListItem>, val listener: OmiseListItemClickListener?) :
    RecyclerView.Adapter<OmiseItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): OmiseItemViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return OmiseItemViewHolder(itemView, listener)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(
        holder: OmiseItemViewHolder,
        position: Int,
    ) {
        val option = list[position]
        holder.bind(option)
    }
}

class OmiseItemViewHolder(val view: View, val listener: OmiseListItemClickListener?) :
    RecyclerView.ViewHolder(view) {
    fun bind(item: OmiseListItem) {
        val listItemView = view.findViewById<LinearLayout>(R.id.list_item_view)
        val optionImage = view.findViewById<ImageView>(R.id.image_item_icon)
        val nameText = view.findViewById<TextView>(R.id.text_item_title)
        val subtitleText = view.findViewById<TextView>(R.id.subtext_item_title)
        val typeImage = view.findViewById<ImageView>(R.id.image_indicator_icon)

        if (item.iconRes != null) {
            optionImage.setImageResource(item.iconRes!!)
            optionImage.visibility = View.VISIBLE
        } else {
            optionImage.visibility = View.GONE
        }

        nameText.text = item.titleRes?.let { view.context.getString(it) } ?: item.title
        subtitleText.text = item.subtitleRes?.let { view.context.getString(it) } ?: item.subtitle

        if (item.enabled == true) {
            item.indicatorIconRes?.let { typeImage.setImageResource(it) }
            listItemView.isEnabled = true
            listItemView.alpha = 1F
        } else {
            item.indicatorIconRes?.let { typeImage.setImageResource(android.R.color.transparent) }
            listItemView.isEnabled = false
            listItemView.alpha = 0.2F
        }

        if (subtitleText.text.isNotBlank()) {
            subtitleText.visibility = View.VISIBLE
        } else {
            subtitleText.visibility = View.GONE
        }

        view.setOnClickListener { listener?.onClick(item) }
    }
}

interface OmiseListItem {
    val iconRes: Int?
        get() = null
    val title: String?
        get() = null
    val titleRes: Int?
        get() = null
    val subtitle: String?
        get() = null
    val subtitleRes: Int?
        get() = null
    val indicatorIconRes: Int?
        get() = null
    val enabled: Boolean?
        get() = true
}

interface OmiseListItemClickListener {
    fun onClick(item: OmiseListItem)
}

private class OmiseItemDecoration(val context: Context) : RecyclerView.ItemDecoration() {
    private val divider: Drawable? =
        AppCompatResources.getDrawable(context, R.drawable.item_decoration)

    override fun onDrawOver(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        val divider = divider ?: return

        val childCount = parent.childCount

        loop@ for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            val pos = parent.getChildAdapterPosition(view)

            if (pos == NO_POSITION) {
                continue@loop
            }

            val params = view.layoutParams as RecyclerView.LayoutParams

            val titleText = view.findViewById<TextView>(R.id.text_item_title)
            val left = titleText.left
            val right = parent.width
            val top = view.bottom + params.bottomMargin
            val bottom = top + divider.intrinsicHeight
            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val divider = divider ?: return

        val pos = parent.getChildAdapterPosition(view)
        if (pos == NO_POSITION) {
            return
        }

        outRect.bottom = divider.intrinsicHeight
    }
}
