package com.example.xiaonenghui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class HomeFragment : Fragment() {
    private var visibleTaskCount = 2

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
        val loadMoreButton = view.findViewById<View>(R.id.button_load_more_tasks)

        loadMoreButton.setOnClickListener {
            visibleTaskCount += 2
            renderLatestTasks(view)
        }

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
        val loadMoreButton = view.findViewById<MaterialButton>(R.id.button_load_more_tasks)

        container.removeAllViews()

        val latestTasks = AppDataStore.tasks.take(visibleTaskCount)
        val inflater = LayoutInflater.from(requireContext())

        for (task in latestTasks) {
            val card = inflater.inflate(R.layout.item_home_task, container, false) as MaterialCardView

            val icon = card.findViewById<TextView>(R.id.task_icon)
            val title = card.findViewById<TextView>(R.id.task_title)
            val tag = card.findViewById<TextView>(R.id.task_tag)
            val time = card.findViewById<TextView>(R.id.task_time)
            val price = card.findViewById<TextView>(R.id.task_price)
            val status = card.findViewById<TextView>(R.id.task_status)

            icon.text = task.category.take(1)
            title.text = task.title
            tag.text = task.category
            time.text = task.location
            price.text = task.price
            status.text = task.status

            when (task.status) {
                "待接单" -> {
                    status.setBackgroundResource(R.drawable.bg_chip_warning)
                    status.setTextColor(ContextCompat.getColor(requireContext(), R.color.tertiary_amber))
                }
                "进行中" -> {
                    status.setBackgroundResource(R.drawable.bg_chip_success)
                    status.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary_green))
                }
                else -> {
                    status.setBackgroundResource(R.drawable.bg_chip_neutral)
                    status.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_on_surface))
                }
            }

            container.addView(card)
        }

        if (visibleTaskCount >= AppDataStore.tasks.size) {
            loadMoreButton.visibility = View.GONE
        } else {
            loadMoreButton.visibility = View.VISIBLE
        }
    }
}
