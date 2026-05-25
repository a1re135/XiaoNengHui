package com.example.xiaonenghui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HomeFragment : Fragment() {
    private var visibleTaskCount = 2
    private lateinit var taskAdapter: HomeTaskAdapter
    private lateinit var serviceAdapter: HomeServiceAdapter
    private lateinit var loadMoreButton: MaterialButton
    private var roleTextView: TextView? = null

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
        loadMoreButton = view.findViewById(R.id.button_load_more_tasks)
        val viewMoreButton = view.findViewById<TextView>(R.id.button_view_more)
        val searchInput = view.findViewById<EditText>(R.id.input_search)
        val searchButton = view.findViewById<View>(R.id.button_search)
        val notificationButton = view.findViewById<View>(R.id.button_notifications)
        roleTextView = view.findViewById(R.id.text_current_role)

        setupServiceRecycler(view)
        setupTaskRecycler(view)
        updateRoleLabel()

        loadMoreButton.setOnClickListener {
            if (visibleTaskCount < AppDataStore.tasks.size) {
                visibleTaskCount += 2
                updateTaskList()
            } else {
                loadMoreButton.visibility = View.GONE
            }
        }

        viewMoreButton.setOnClickListener {
            (activity as? MainActivity)?.selectBottomTab(R.id.nav_services)
        }

        searchButton.setOnClickListener {
            performSearch(searchInput.text.toString())
        }

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(searchInput.text.toString())
                true
            } else {
                false
            }
        }

        notificationButton.setOnClickListener {
            showNotifications()
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

        updateTaskList()
    }

    override fun onResume() {
        super.onResume()
        updateRoleLabel()
        updateTaskList()
    }

    private fun setupServiceRecycler(view: View) {
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_recommended)
        serviceAdapter = HomeServiceAdapter { service ->
            showServiceDialog(service)
        }
        recycler.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recycler.adapter = serviceAdapter
        serviceAdapter.submitList(AppDataStore.services)
    }

    private fun setupTaskRecycler(view: View) {
        val recycler = view.findViewById<RecyclerView>(R.id.recycler_latest_tasks)
        taskAdapter = HomeTaskAdapter { task ->
            showTaskDialog(task)
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = taskAdapter
    }

    private fun updateTaskList() {
        val tasksToShow = AppDataStore.tasks.take(visibleTaskCount)
        taskAdapter.submitList(tasksToShow)

        loadMoreButton.visibility = if (visibleTaskCount >= AppDataStore.tasks.size) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun updateRoleLabel() {
        roleTextView?.text = AppDataStore.currentRole
    }

    private fun showServiceDialog(service: ServiceItem) {
        val details = mutableListOf(
            "服务名称：${service.title}",
            "服务类型：${service.category}",
            "提供者：${service.provider}",
            "价格：${service.price}",
            "评分：${service.rating}"
        )

        if (service.description.isNotBlank()) {
            details.add("服务描述：${service.description}")
        }
        if (service.location.isNotBlank()) {
            details.add("地点：${service.location}")
        }
        if (service.schedule.isNotBlank()) {
            details.add("可预约时间：${service.schedule}")
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.home_service_detail))
            .setMessage(details.joinToString("\n"))
            .setNegativeButton(getString(R.string.home_close), null)
            .setPositiveButton(getString(R.string.home_book_now)) { _, _ ->
                Toast.makeText(requireContext(), getString(R.string.home_book_toast), Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun showTaskDialog(task: TaskItem) {
        val details = mutableListOf(
            "任务标题：${task.title}",
            "任务类型：${task.category}",
            "地点：${task.location}",
            "价格：${task.price}",
            "状态：${task.status}"
        )

        if (task.description.isNotBlank()) {
            details.add("任务描述：${task.description}")
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.home_task_detail))
            .setMessage(details.joinToString("\n"))
            .setNegativeButton(getString(R.string.home_close), null)
            .show()
    }

    private fun performSearch(rawKeyword: String) {
        val keyword = rawKeyword.trim()
        if (keyword.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.home_search_empty), Toast.LENGTH_SHORT).show()
            return
        }

        val serviceMatches = AppDataStore.services.filter { service ->
            service.title.contains(keyword, true) ||
                service.category.contains(keyword, true) ||
                service.provider.contains(keyword, true)
        }

        val taskMatches = AppDataStore.tasks.filter { task ->
            task.title.contains(keyword, true) ||
                task.category.contains(keyword, true) ||
                task.location.contains(keyword, true)
        }

        if (serviceMatches.isEmpty() && taskMatches.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.home_no_results), Toast.LENGTH_SHORT).show()
            return
        }

        val message = StringBuilder()
        if (serviceMatches.isNotEmpty()) {
            message.append("服务：\n")
            serviceMatches.forEach { service ->
                message.append("• ${service.title} / ${service.category} / ${service.provider} / ${service.price} / ${service.rating}\n")
            }
            message.append("\n")
        }
        if (taskMatches.isNotEmpty()) {
            message.append("任务：\n")
            taskMatches.forEach { task ->
                message.append("• ${task.title} / ${task.category} / ${task.location} / ${task.price} / ${task.status}\n")
            }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.home_search_results))
            .setMessage(message.toString().trim())
            .setPositiveButton(getString(R.string.home_close), null)
            .show()
    }

    private fun showNotifications() {
        val pendingTasks = AppDataStore.tasks.filter { it.status == "待接单" }
        if (pendingTasks.isEmpty()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.home_notifications))
                .setMessage(getString(R.string.home_no_notifications))
                .setPositiveButton(getString(R.string.home_close), null)
                .show()
            return
        }

        val title = getString(R.string.home_notification_pending, pendingTasks.size)
        val listText = pendingTasks.take(3).joinToString("\n") { "• ${it.title}" }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.home_notifications))
            .setMessage("$title\n$listText")
            .setPositiveButton(getString(R.string.home_close), null)
            .show()
    }
}
