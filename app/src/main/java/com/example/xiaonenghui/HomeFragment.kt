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
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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
        val viewMoreButton = view.findViewById<TextView>(R.id.button_view_more)
        val searchInput = view.findViewById<EditText>(R.id.input_search)
        val searchButton = view.findViewById<View>(R.id.button_search)
        val notificationButton = view.findViewById<View>(R.id.button_notifications)

        loadMoreButton.setOnClickListener {
            if (visibleTaskCount >= AppDataStore.tasks.size) {
                Toast.makeText(requireContext(), getString(R.string.home_no_more_tasks), Toast.LENGTH_SHORT).show()
            } else {
                visibleTaskCount += 2
                renderLatestTasks(view)
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

        renderRecommendedServices(view)
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
    }

    private fun renderRecommendedServices(view: View) {
        val container = view.findViewById<LinearLayout>(R.id.home_recommend_container)
        val inflater = LayoutInflater.from(requireContext())
        val coverBackgrounds = listOf(
            R.drawable.bg_recommend_primary,
            R.drawable.bg_recommend_secondary,
            R.drawable.bg_recommend_tertiary
        )
        val tagBackgrounds = listOf(
            R.drawable.bg_tag_primary,
            R.drawable.bg_tag_secondary,
            R.drawable.bg_tag_tertiary
        )
        val tagTextColors = listOf(
            R.color.blue_primary,
            R.color.secondary_green,
            R.color.tertiary_amber
        )

        container.removeAllViews()

        for ((index, service) in AppDataStore.services.withIndex()) {
            val card = inflater.inflate(R.layout.item_home_service, container, false) as MaterialCardView

            val cover = card.findViewById<View>(R.id.service_cover)
            val tag = card.findViewById<TextView>(R.id.service_tag)
            val title = card.findViewById<TextView>(R.id.service_title)
            val provider = card.findViewById<TextView>(R.id.service_provider)
            val desc = card.findViewById<TextView>(R.id.service_desc)
            val rating = card.findViewById<TextView>(R.id.service_rating)
            val price = card.findViewById<TextView>(R.id.service_price)

            val styleIndex = index % coverBackgrounds.size
            cover.setBackgroundResource(coverBackgrounds[styleIndex])
            tag.setBackgroundResource(tagBackgrounds[styleIndex])
            tag.setTextColor(ContextCompat.getColor(requireContext(), tagTextColors[styleIndex]))

            tag.text = service.category
            title.text = service.title
            provider.text = "提供者：${service.provider}"
            rating.text = service.rating
            price.text = service.price

            if (service.description.isBlank()) {
                desc.visibility = View.GONE
            } else {
                desc.visibility = View.VISIBLE
                desc.text = service.description
            }

            card.setOnClickListener {
                showServiceDialog(service)
            }

            container.addView(card)
        }
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
            details.add("服务地点：${service.location}")
        }
        if (service.schedule.isNotBlank()) {
            details.add("预约时间：${service.schedule}")
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.home_service_detail))
            .setMessage(details.joinToString("\n"))
            .setNegativeButton(getString(R.string.home_close), null)
            .setPositiveButton(getString(R.string.home_book_now)) { _, _ ->
                Toast.makeText(requireContext(), "已提交预约请求", Toast.LENGTH_SHORT).show()
            }
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
                service.provider.contains(keyword, true) ||
                service.location.contains(keyword, true)
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
        val latestTask = AppDataStore.tasks.firstOrNull()
        val message = if (latestTask == null) {
            getString(R.string.home_no_notifications)
        } else {
            "你有 1 个新任务待处理：${latestTask.title} - ${latestTask.status}"
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.home_notifications))
            .setMessage(message)
            .setPositiveButton(getString(R.string.home_close), null)
            .show()
    }
}
