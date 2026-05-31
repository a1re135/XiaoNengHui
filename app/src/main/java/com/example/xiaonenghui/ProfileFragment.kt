package com.example.xiaonenghui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {
    private var isRequester = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logoutButton = view.findViewById<Button>(R.id.logout_button)
        val profileCard = view.findViewById<View>(R.id.profile_card)
        val roleChip = view.findViewById<TextView>(R.id.role_chip)
        val roleSwitchButton = view.findViewById<TextView>(R.id.role_switch_button)

        val menuMyPosts = view.findViewById<View>(R.id.menu_my_posts)
        val menuMyFavorites = view.findViewById<View>(R.id.menu_my_favorites)
        val menuCredit = view.findViewById<View>(R.id.menu_credit)
        val menuHelp = view.findViewById<View>(R.id.menu_help)
        val menuAbout = view.findViewById<View>(R.id.menu_about)

        val schoolButton = view.findViewById<View>(R.id.button_profile_school)
        val notificationButton = view.findViewById<View>(R.id.button_profile_notifications)

        val statCredit = view.findViewById<View>(R.id.stat_credit)
        val statRating = view.findViewById<View>(R.id.stat_rating)
        val statOrders = view.findViewById<View>(R.id.stat_orders)

        profileCard.setOnClickListener {
            showToast(getString(R.string.profile_toast_profile))
        }

        roleSwitchButton.setOnClickListener {
            isRequester = !isRequester
            val role = if (isRequester) {
                getString(R.string.profile_role_requester)
            } else {
                getString(R.string.profile_role_provider)
            }
            roleChip.text = getString(R.string.profile_role_current, role)
            roleSwitchButton.text = if (isRequester) {
                getString(R.string.profile_role_switch_to_provider)
            } else {
                getString(R.string.profile_role_switch_to_requester)
            }
            showToast(getString(R.string.profile_role_switched, role))
        }

        menuMyPosts.setOnClickListener { showToast(getString(R.string.profile_toast_my_posts)) }
        menuMyFavorites.setOnClickListener { showToast(getString(R.string.profile_toast_favorites)) }
        menuCredit.setOnClickListener { showToast(getString(R.string.profile_toast_credit)) }
        menuHelp.setOnClickListener { showToast(getString(R.string.profile_toast_help)) }
        menuAbout.setOnClickListener { showToast(getString(R.string.profile_toast_about)) }

        statCredit.setOnClickListener { showToast(getString(R.string.profile_toast_credit_stat)) }
        statRating.setOnClickListener { showToast(getString(R.string.profile_toast_rating_stat)) }
        statOrders.setOnClickListener { showToast(getString(R.string.profile_toast_orders_stat)) }

        schoolButton.setOnClickListener {
            (activity as? MainActivity)?.selectBottomTab(R.id.nav_home)
        }

        notificationButton.setOnClickListener {
            showToast(getString(R.string.home_no_notifications))
        }

        logoutButton.setOnClickListener {
            (activity as? MainActivity)?.logout()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
