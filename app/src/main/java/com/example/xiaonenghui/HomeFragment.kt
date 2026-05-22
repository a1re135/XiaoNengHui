package com.example.xiaonenghui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardTutoring = view.findViewById<MaterialCardView>(R.id.card_tutoring)
        val cardCreative = view.findViewById<MaterialCardView>(R.id.card_creative)
        val cardErrands = view.findViewById<MaterialCardView>(R.id.card_errands)
        val cardPostTask = view.findViewById<MaterialCardView>(R.id.card_post_task)
        val cardMyOrders = view.findViewById<MaterialCardView>(R.id.card_my_orders)
        val cardRating = view.findViewById<MaterialCardView>(R.id.card_rating)
        val buttonSwitchRole = view.findViewById<MaterialButton>(R.id.btn_switch_role)

        cardTutoring.setOnClickListener {
            Toast.makeText(requireContext(), "进入技能辅导服务", Toast.LENGTH_SHORT).show()
            (activity as? MainActivity)?.selectBottomTab(R.id.nav_services)
        }

        cardCreative.setOnClickListener {
            Toast.makeText(requireContext(), "进入创意服务", Toast.LENGTH_SHORT).show()
            (activity as? MainActivity)?.selectBottomTab(R.id.nav_services)
        }

        cardErrands.setOnClickListener {
            Toast.makeText(requireContext(), "进入跑腿服务", Toast.LENGTH_SHORT).show()
            (activity as? MainActivity)?.selectBottomTab(R.id.nav_services)
        }

        cardPostTask.setOnClickListener {
            (activity as? MainActivity)?.selectBottomTab(R.id.nav_post)
        }

        cardMyOrders.setOnClickListener {
            (activity as? MainActivity)?.selectBottomTab(R.id.nav_orders)
        }

        cardRating.setOnClickListener {
            (activity as? MainActivity)?.selectBottomTab(R.id.nav_profile)
        }

        buttonSwitchRole.setOnClickListener {
            Toast.makeText(requireContext(), "身份切换功能将在个人中心完善", Toast.LENGTH_SHORT).show()
            (activity as? MainActivity)?.selectBottomTab(R.id.nav_profile)
        }

        renderLatestTasks(view)
    }

    override fun onResume() {
        super.onResume()
        view?.let {
            renderLatestTasks(it)
        }
    }

    private fun renderLatestTasks(view: View) {
        val container = view.findViewById<LinearLayout>(R.id.home_latest_tasks_container)
        container.removeAllViews()

        val latestTasks = AppDataStore.tasks.take(2)
        val inflater = LayoutInflater.from(requireContext())

        for (task in latestTasks) {
            val itemView = inflater.inflate(R.layout.item_home_task, container, false)

            val iconView = itemView.findViewById<TextView>(R.id.task_icon)
            val titleView = itemView.findViewById<TextView>(R.id.task_title)
            val tagView = itemView.findViewById<TextView>(R.id.task_tag)
            val timeView = itemView.findViewById<TextView>(R.id.task_time)
            val priceView = itemView.findViewById<TextView>(R.id.task_price)
            val statusView = itemView.findViewById<TextView>(R.id.task_status)

            titleView.text = task.title
            tagView.text = task.category
            timeView.text = task.location
            priceView.text = task.price
            statusView.text = task.status

            val iconText = when {
                task.category.contains("跑腿") -> "取"
                task.category.contains("技术") -> "码"
                task.category.contains("辅导") -> "辅"
                task.category.contains("设计") -> "创"
                else -> "任"
            }
            iconView.text = iconText

            when {
                task.status.contains("待") -> {
                    statusView.setBackgroundResource(R.drawable.bg_chip_warning)
                    statusView.setTextColor(ContextCompat.getColor(requireContext(), R.color.tertiary_amber))
                }
                task.status.contains("中") -> {
                    statusView.setBackgroundResource(R.drawable.bg_chip_success)
                    statusView.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary_green))
                }
                else -> {
                    statusView.setBackgroundResource(R.drawable.bg_chip_neutral)
                    statusView.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_on_surface))
                }
            }

            container.addView(itemView)
        }
    }
}
