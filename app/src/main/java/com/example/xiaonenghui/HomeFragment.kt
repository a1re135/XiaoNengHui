package com.example.xiaonenghui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    }
}
