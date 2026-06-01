package com.example.xiaonenghui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {

    // Fake local profile info for now
    private val profileName = "陈同学"
    private val profileStudentId = "1820241091"

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

        val statCreditValue = view.findViewById<TextView>(R.id.stat_credit_value)
        val statRatingValue = view.findViewById<TextView>(R.id.stat_rating_value)
        val statOrdersValue = view.findViewById<TextView>(R.id.stat_orders_value)

        val nameView = view.findViewById<TextView>(R.id.profile_name)
        val idView = view.findViewById<TextView>(R.id.profile_student_id)

        // Initialize static profile info
        nameView.text = profileName
        idView.text = profileStudentId

        // Initialize role and button text
        roleChip.text = getString(R.string.profile_role_current, AppDataStore.currentRole)
        roleSwitchButton.text = if (AppDataStore.currentRole == getString(R.string.profile_role_requester)) {
            getString(R.string.profile_role_switch_to_provider)
        } else {
            getString(R.string.profile_role_switch_to_requester)
        }

        // Initialize stats
        statCreditValue.text = getString(R.string.profile_stat_credit_value) // e.g. A
        statRatingValue.text = getString(R.string.profile_stat_rating_value) // e.g. 4.8
        statOrdersValue.text = AppDataStore.orders.size.toString()

        profileCard.setOnClickListener {
            val message = "姓名：$profileName\n学号：$profileStudentId\n${getString(R.string.profile_role_current, AppDataStore.currentRole)}"
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("个人资料")
                .setMessage(message)
                .setPositiveButton(getString(R.string.home_close), null)
                .show()
        }

        roleSwitchButton.setOnClickListener {
            // Toggle AppDataStore.currentRole
            AppDataStore.currentRole = if (AppDataStore.currentRole == getString(R.string.profile_role_requester)) {
                getString(R.string.profile_role_provider)
            } else {
                getString(R.string.profile_role_requester)
            }

            // Update UI immediately
            roleChip.text = getString(R.string.profile_role_current, AppDataStore.currentRole)
            roleSwitchButton.text = if (AppDataStore.currentRole == getString(R.string.profile_role_requester)) {
                getString(R.string.profile_role_switch_to_provider)
            } else {
                getString(R.string.profile_role_switch_to_requester)
            }

            Toast.makeText(requireContext(), getString(R.string.profile_role_switched, AppDataStore.currentRole), Toast.LENGTH_SHORT).show()
        }

        // Menu actions show simple dialog for now
        val comingSoon = "该功能将在后续版本完善"
        menuMyPosts.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.profile_menu_published))
                .setMessage(comingSoon)
                .setPositiveButton(getString(R.string.home_close), null)
                .show()
        }
        menuMyFavorites.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.profile_menu_favorites))
                .setMessage(comingSoon)
                .setPositiveButton(getString(R.string.home_close), null)
                .show()
        }
        menuCredit.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.profile_menu_credit))
                .setMessage(comingSoon)
                .setPositiveButton(getString(R.string.home_close), null)
                .show()
        }
        menuHelp.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.profile_menu_help))
                .setMessage(comingSoon)
                .setPositiveButton(getString(R.string.home_close), null)
                .show()
        }
        menuAbout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.profile_menu_about))
                .setMessage(comingSoon)
                .setPositiveButton(getString(R.string.home_close), null)
                .show()
        }

        statCreditValue.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.profile_stat_credit))
                .setMessage("等级：${statCreditValue.text}")
                .setPositiveButton(getString(R.string.home_close), null)
                .show()
        }
        statRatingValue.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.profile_stat_rating))
                .setMessage("评分：${statRatingValue.text}")
                .setPositiveButton(getString(R.string.home_close), null)
                .show()
        }
        statOrdersValue.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.profile_stat_orders))
                .setMessage("已预约订单：${AppDataStore.orders.size}")
                .setPositiveButton(getString(R.string.home_close), null)
                .show()
        }

        schoolButton.setOnClickListener {
            (activity as? MainActivity)?.selectBottomTab(R.id.nav_home)
        }

        notificationButton.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.home_no_notifications), Toast.LENGTH_SHORT).show()
        }

        logoutButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.logout_button))
                .setMessage("确定要退出当前账号吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定退出") { _, _ ->
                    (activity as? MainActivity)?.logout()
                }
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        // refresh dynamic fields
        view?.findViewById<TextView>(R.id.role_chip)?.text = getString(R.string.profile_role_current, AppDataStore.currentRole)
        view?.findViewById<TextView>(R.id.role_switch_button)?.text = if (AppDataStore.currentRole == getString(R.string.profile_role_requester)) {
            getString(R.string.profile_role_switch_to_provider)
        } else {
            getString(R.string.profile_role_switch_to_requester)
        }
        view?.findViewById<TextView>(R.id.stat_orders_value)?.text = AppDataStore.orders.size.toString()
        // For published count we keep it simple: count services whose provider matches profileName or "发布者"
        val publishedCount = AppDataStore.services.count { it.provider == "发布者" || it.provider == "${"陈同学"}" }
        // If you want to display publishedCount somewhere later, add a TextView id. For now we reuse menu badge via toast on click.
    }

}
