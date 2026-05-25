package com.example.xiaonenghui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class HomeTaskAdapter : RecyclerView.Adapter<HomeTaskAdapter.TaskViewHolder>() {

    private val items = mutableListOf<TaskItem>()

    fun submitList(newItems: List<TaskItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon = itemView.findViewById<TextView>(R.id.task_icon)
        private val title = itemView.findViewById<TextView>(R.id.task_title)
        private val tag = itemView.findViewById<TextView>(R.id.task_tag)
        private val time = itemView.findViewById<TextView>(R.id.task_time)
        private val price = itemView.findViewById<TextView>(R.id.task_price)
        private val status = itemView.findViewById<TextView>(R.id.task_status)

        fun bind(item: TaskItem) {
            icon.text = item.category.take(1)
            title.text = item.title
            tag.text = item.category
            time.text = item.location
            price.text = item.price
            status.text = item.status

            when (item.status) {
                "待接单" -> {
                    status.setBackgroundResource(R.drawable.bg_chip_warning)
                    status.setTextColor(ContextCompat.getColor(itemView.context, R.color.tertiary_amber))
                }
                "进行中" -> {
                    status.setBackgroundResource(R.drawable.bg_chip_success)
                    status.setTextColor(ContextCompat.getColor(itemView.context, R.color.secondary_green))
                }
                else -> {
                    status.setBackgroundResource(R.drawable.bg_chip_neutral)
                    status.setTextColor(ContextCompat.getColor(itemView.context, R.color.blue_on_surface))
                }
            }
        }
    }
}

