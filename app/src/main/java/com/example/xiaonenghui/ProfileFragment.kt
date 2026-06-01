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

    private val profileName = "陈同学"
    private val profileStudentId = "1820241091"

    private var roleChip: TextView? = null
    private var roleSwitchButton: TextView? = null
    private var statOrdersValue: TextView? = null
    private var statPublishedValue: TextView? = null
    private var statCreditValue: TextView? = null
    private var statRatingValue: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameView = view.findViewById<TextView>(R.id.profile_name)
        val idView = view.findViewById<TextView>(R.id.profile_student_id)
        roleChip = view.findViewById(R.id.role_chip)
        roleSwitchButton = view.findViewById(R.id.role_switch_button)
        statOrdersValue = view.findViewById(R.id.stat_orders_value)
        statPublishedValue = view.findViewById(R.id.stat_published_value)
        statCreditValue = view.findViewById(R.id.stat_credit_value)
        statRatingValue = view.findViewById(R.id.stat_rating_value)

        val profileCard = view.findViewById<View>(R.id.profile_card)
        val myPostsRow = view.findViewById<View>(R.id.menu_my_posts)
        val aboutRow = view.findViewById<View>(R.id.menu_about)
        val logoutButton = view.findViewById<Button>(R.id.logout_button)
        val schoolButton = view.findViewById<View>(R.id.button_profile_school)
        val notificationButton = view.findViewById<View>(R.id.button_profile_notifications)

        nameView.text = profileName
        idView.text = getString(R.string.profile_student_id_value, profileStudentId)

        profileCard.setOnClickListener {
            val info = buildString {
                append("用户名：").append(profileName)
                append('\n')
                append(getString(R.string.profile_student_id_value, profileStudentId))
                append('\n')
                append(getString(R.string.profile_current_role_value, AppDataStore.currentRole))
            }
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("用户信息")
                .setMessage(info)
                .setPositiveButton("知道了", null)
                .show()
        }

        roleSwitchButton?.setOnClickListener {
            toggleRole()
        }

        myPostsRow.setOnClickListener {
            showMyPostsDialog()
        }

        aboutRow.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("关于校能汇")
                .setMessage(
                    "校能汇是一个面向高校学生的校园技能与微服务平台，支持技能辅导、创意服务、校园跑腿、任务发布、订单管理和身份切换等功能，帮助学生更方便地获取服务和实现技能价值。"
                )
                .setPositiveButton("知道了", null)
                .show()
        }

        logoutButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("退出登录")
                .setMessage("确定要退出当前账号吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定退出") { _, _ ->
                    (activity as? MainActivity)?.logout()
                }
                .show()
        }

        schoolButton.setOnClickListener {
            (activity as? MainActivity)?.selectBottomTab(R.id.nav_home)
        }

        notificationButton.setOnClickListener {
            Toast.makeText(requireContext(), "暂无新的通知", Toast.LENGTH_SHORT).show()
        }

        refreshUi()
    }

    override fun onResume() {
        super.onResume()
        refreshUi()
    }

    private fun refreshUi() {
        val currentRole = AppDataStore.currentRole
        roleChip?.text = getString(R.string.profile_current_role_value, currentRole)
        roleSwitchButton?.text = if (currentRole == "需求方") {
            "切换为服务提供者"
        } else {
            "切换为需求方"
        }
        statOrdersValue?.text = AppDataStore.orders.size.toString()
        statPublishedValue?.text = countPublishedItems().toString()
        statCreditValue?.text = "A"
        statRatingValue?.text = "4.8"
    }

    private fun toggleRole() {
        AppDataStore.currentRole = if (AppDataStore.currentRole == "需求方") {
            "服务提供者"
        } else {
            "需求方"
        }
        refreshUi()
        Toast.makeText(requireContext(), "已切换为${AppDataStore.currentRole}", Toast.LENGTH_SHORT).show()
    }

    private fun countPublishedItems(): Int {
        return AppDataStore.services.count { it.provider == "发布者" }
    }

    private fun showMyPostsDialog() {
        val myPosts = AppDataStore.services.filter { it.provider == "发布者" }
        val message = if (myPosts.isEmpty()) {
            "暂无发布内容"
        } else {
            myPosts.joinToString("\n\n") { item ->
                "标题：${item.title}\n分类：${item.category}\n价格：${item.price}\n地点：${item.location.ifBlank { "未填写" }}"
            }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("我的发布")
            .setMessage(message)
            .setPositiveButton("知道了", null)
            .show()
    }
}
