package com.sporksoft.kiddom.ui.adapter

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.ViewGroup
import android.view.View
import com.sporksoft.kiddom.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_feed.*
import com.sporksoft.kiddom.helper.extensions.*
import com.sporksoft.kiddom.models.ItemElement

class FeedAdapter(val items: MutableList<ItemElement> = ArrayList(), val onClickListener: View.OnClickListener? = null): RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_feed), onClickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)
        holder.bind(item)
        holder.containerView.tag = position
    }

    class ViewHolder(override val containerView: View, listener: View.OnClickListener? = null) : RecyclerView.ViewHolder(containerView),
            LayoutContainer {

        init {
            containerView.setOnClickListener(listener)
        }

        fun bind(item: ItemElement) {
            item.result.image?.let {
                if (!TextUtils.isEmpty(it.contentUrl)) {
                    image.loadUrl(item.result.image.contentUrl)
                }
            }
            title.text = item.result.name
            subTitle.text = item.result.description
        }
    }

    fun addPage(newItems: List<ItemElement>?) {
        if (newItems == null) {
            return
        }
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun reset(newItems: List<ItemElement>?) {
        if (newItems == null) {
            return
        }
        items.clear()
        addPage(newItems)
    }

}