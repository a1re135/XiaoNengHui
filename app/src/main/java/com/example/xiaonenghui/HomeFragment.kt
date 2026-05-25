package com.example.xiaonenghui

import android.graphics.Color
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
    private var visibleTaskCount = 2;

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

        for (task in latestTasks) {
            val card = MaterialCardView(requireContext()).apply {
                radius = 18f
                cardElevation = 3f
                setCardBackgroundColor(Color.WHITE)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 12, 0, 0)
                layoutParams = params
            }

            val text = TextView(requireContext()).apply {
                text = "${task.title}\n${task.category} | ${task.location} | ${task.price} | ${task.status}"
                textSize = 15f
                setTextColor(Color.parseColor("#111827"))
                setPadding(24, 20, 24, 20)
            }

            card.addView(text)
            container.addView(card)
        }

        if (visibleTaskCount >= AppDataStore.tasks.size) {
            loadMoreButton.visibility = View.GONE
        } else {
            loadMoreButton.visibility = View.VISIBLE
        }
    }
}
