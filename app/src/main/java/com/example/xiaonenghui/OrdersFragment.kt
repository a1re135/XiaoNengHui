package com.example.xiaonenghui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView

class OrdersFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onResume() {
        super.onResume()
        view?.let {
            renderOrders(it)
        }
    }

    private fun renderOrders(view: View) {
        val container = view.findViewById<LinearLayout>(R.id.orders_list_container)
        val emptyText = view.findViewById<TextView>(R.id.orders_empty_text)

        container.removeAllViews()

        if (AppDataStore.tasks.isEmpty()) {
            emptyText.visibility = View.VISIBLE
            return
        } else {
            emptyText.visibility = View.GONE
        }

        for (task in AppDataStore.tasks) {
            val card = MaterialCardView(requireContext()).apply {
                radius = 18f
                cardElevation = 3f
                setCardBackgroundColor(Color.WHITE)

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 16)
                layoutParams = params
            }

            val text = TextView(requireContext()).apply {
                text = """
                    ${task.title}
                    类型：${task.category}
                    地点：${task.location}
                    价格：${task.price}
                    状态：${task.status}
                    描述：${task.description}
                """.trimIndent()

                textSize = 15f
                setTextColor(Color.parseColor("#111827"))
                setPadding(24, 20, 24, 20)
            }

            card.addView(text)
            container.addView(card)
        }
    }
}