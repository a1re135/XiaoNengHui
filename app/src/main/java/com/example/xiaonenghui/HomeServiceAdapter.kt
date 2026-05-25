package com.example.xiaonenghui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class HomeServiceAdapter(
    private val onItemClick: (ServiceItem) -> Unit
) : RecyclerView.Adapter<HomeServiceAdapter.ServiceViewHolder>() {

    private val items = mutableListOf<ServiceItem>()

    fun submitList(newItems: List<ServiceItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_service, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(items[position], position, onItemClick)
    }

    override fun getItemCount(): Int = items.size

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cover = itemView.findViewById<View>(R.id.service_cover)
        private val tag = itemView.findViewById<TextView>(R.id.service_tag)
        private val title = itemView.findViewById<TextView>(R.id.service_title)
        private val provider = itemView.findViewById<TextView>(R.id.service_provider)
        private val desc = itemView.findViewById<TextView>(R.id.service_desc)
        private val rating = itemView.findViewById<TextView>(R.id.service_rating)
        private val price = itemView.findViewById<TextView>(R.id.service_price)

        fun bind(item: ServiceItem, position: Int, onClick: (ServiceItem) -> Unit) {
            val coverBackgrounds = listOf(
                R.drawable.bg_recommend_primary,
                R.drawable.bg_recommend_secondary,
                R.drawable.bg_recommend_tertiary
            )
            val tagBackgrounds = listOf(
                R.drawable.bg_tag_primary,
                R.drawable.bg_tag_secondary,
                R.drawable.bg_tag_tertiary
            )
            val tagTextColors = listOf(
                R.color.blue_primary,
                R.color.secondary_green,
                R.color.tertiary_amber
            )

            val styleIndex = position % coverBackgrounds.size
            cover.setBackgroundResource(coverBackgrounds[styleIndex])
            tag.setBackgroundResource(tagBackgrounds[styleIndex])
            tag.setTextColor(ContextCompat.getColor(itemView.context, tagTextColors[styleIndex]))

            tag.text = item.category
            title.text = item.title
            provider.text = "提供者：${item.provider}"
            rating.text = item.rating
            price.text = item.price

            if (item.description.isBlank()) {
                desc.visibility = View.GONE
            } else {
                desc.visibility = View.VISIBLE
                desc.text = item.description
            }

            (itemView as MaterialCardView).setOnClickListener {
                onClick(item)
            }
        }
    }
}

