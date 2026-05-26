package com.example.xiaonenghui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ServicesFragment : Fragment() {

    private var currentCategory = "全部"
    private var currentKeyword = ""

    private lateinit var serviceCards: List<ServiceCardBinding>
    private lateinit var chips: List<TextView>

    data class ServiceCardBinding(
        val service: ServiceItem,
        val card: MaterialCardView,
        val bookButton: MaterialButton
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_services, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchInput = view.findViewById<EditText>(R.id.input_search_services)
        val searchButton = view.findViewById<View>(R.id.button_search_services)
        val notificationButton = view.findViewById<View>(R.id.button_notifications)

        val chipAll = view.findViewById<TextView>(R.id.chip_all)
        val chipTutoring = view.findViewById<TextView>(R.id.chip_tutoring)
        val chipCreative = view.findViewById<TextView>(R.id.chip_creative)
        val chipErrands = view.findViewById<TextView>(R.id.chip_errands)
        val chipProgramming = view.findViewById<TextView>(R.id.chip_programming)

        chips = listOf(chipAll, chipTutoring, chipCreative, chipErrands, chipProgramming)

        val mathService = findService("数学", fallbackMathService())
        val pptService = findService("PPT", fallbackPptService())
        val debugService = findService("代码", fallbackDebugService())
        val deliveryService = findService("快递", fallbackDeliveryService())

        serviceCards = listOf(
            ServiceCardBinding(
                service = mathService,
                card = view.findViewById(R.id.card_service_math),
                bookButton = view.findViewById(R.id.button_book_math)
            ),
            ServiceCardBinding(
                service = pptService,
                card = view.findViewById(R.id.card_service_ppt),
                bookButton = view.findViewById(R.id.button_book_ppt)
            ),
            ServiceCardBinding(
                service = debugService,
                card = view.findViewById(R.id.card_service_debug),
                bookButton = view.findViewById(R.id.button_book_debug)
            ),
            ServiceCardBinding(
                service = deliveryService,
                card = view.findViewById(R.id.card_service_delivery),
                bookButton = view.findViewById(R.id.button_book_delivery)
            )
        )

        searchButton.setOnClickListener {
            submitSearch(searchInput.text.toString())
        }

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                submitSearch(searchInput.text.toString())
                true
            } else {
                false
            }
        }

        notificationButton.setOnClickListener {
            showNotifications()
        }

        chipAll.setOnClickListener {
            selectCategory("全部", chipAll)
        }

        chipTutoring.setOnClickListener {
            selectCategory("技能辅导", chipTutoring)
        }

        chipCreative.setOnClickListener {
            selectCategory("创意服务", chipCreative)
        }

        chipErrands.setOnClickListener {
            selectCategory("校园跑腿", chipErrands)
        }

        chipProgramming.setOnClickListener {
            selectCategory("编程技术", chipProgramming)
        }

        serviceCards.forEach { binding ->
            binding.card.setOnClickListener {
                showServiceDetail(binding.service)
            }

            binding.bookButton.setOnClickListener {
                bookService(binding.service)
            }
        }

        selectCategory("全部", chipAll)
    }

    private fun submitSearch(rawKeyword: String) {
        currentKeyword = rawKeyword.trim()

        if (currentKeyword.isEmpty()) {
            Toast.makeText(requireContext(), "请输入搜索关键词", Toast.LENGTH_SHORT).show()
            renderServices()
            return
        }

        val visibleCount = renderServices()

        if (visibleCount == 0) {
            Toast.makeText(requireContext(), "没有找到相关服务", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectCategory(category: String, selectedChip: TextView) {
        currentCategory = category
        updateChipStyle(selectedChip)

        val visibleCount = renderServices()

        if (visibleCount == 0) {
            Toast.makeText(requireContext(), "当前分类暂无服务", Toast.LENGTH_SHORT).show()
        }
    }

    private fun renderServices(): Int {
        var visibleCount = 0

        serviceCards.forEach { binding ->
            val service = binding.service

            val matchesCategory =
                currentCategory == "全部" ||
                        service.category.contains(currentCategory) ||
                        service.title.contains(currentCategory)

            val matchesKeyword =
                currentKeyword.isEmpty() ||
                        service.title.contains(currentKeyword, ignoreCase = true) ||
                        service.category.contains(currentKeyword, ignoreCase = true) ||
                        service.provider.contains(currentKeyword, ignoreCase = true) ||
                        service.description.contains(currentKeyword, ignoreCase = true) ||
                        service.location.contains(currentKeyword, ignoreCase = true)

            val shouldShow = matchesCategory && matchesKeyword

            binding.card.visibility = if (shouldShow) {
                visibleCount++
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        return visibleCount
    }

    private fun updateChipStyle(selectedChip: TextView) {
        chips.forEach { chip ->
            val isSelected = chip == selectedChip

            chip.setBackgroundResource(
                if (isSelected) R.drawable.bg_chip_success else R.drawable.bg_chip_neutral
            )

            chip.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isSelected) R.color.blue_primary else R.color.blue_on_surface
                )
            )

            chip.setTypeface(null, if (isSelected) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)
        }
    }

    private fun showServiceDetail(service: ServiceItem) {
        val message = """
            服务类型：${service.category}
            提供者：${service.provider}
            价格：${service.price}
            评分：${service.rating}
            地点：${service.location}
            可预约时间：${service.schedule}
            
            服务描述：
            ${service.description}
        """.trimIndent()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(service.title)
            .setMessage(message)
            .setNegativeButton("关闭", null)
            .setPositiveButton("立即预约") { _, _ ->
                bookService(service)
            }
            .show()
    }

    private fun showNotifications() {
        val pendingTasks = AppDataStore.tasks.filter {
            it.status.contains("待")
        }

        val message = if (pendingTasks.isEmpty()) {
            "暂无新的通知"
        } else {
            "你有 ${pendingTasks.size} 个新任务待处理：\n" +
                    pendingTasks.take(3).joinToString("\n") {
                        "• ${it.title} - ${it.status}"
                    }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("通知")
            .setMessage(message)
            .setPositiveButton("关闭", null)
            .show()
    }

    private fun findService(keyword: String, fallback: ServiceItem): ServiceItem {
        return AppDataStore.services.firstOrNull {
            it.title.contains(keyword, ignoreCase = true) ||
                    it.category.contains(keyword, ignoreCase = true) ||
                    it.description.contains(keyword, ignoreCase = true)
        } ?: fallback
    }

    private fun fallbackMathService(): ServiceItem {
        return ServiceItem(
            title = "高等数学辅导",
            category = "技能辅导",
            provider = "张同学",
            price = "20元/小时",
            rating = "4.9",
            description = "一对一讲解高等数学重点题型和作业问题",
            location = "图书馆自习室",
            schedule = "周一/三晚 19:00"
        )
    }

    private fun fallbackPptService(): ServiceItem {
        return ServiceItem(
            title = "PPT制作",
            category = "创意服务",
            provider = "李同学",
            price = "15元/份",
            rating = "4.8",
            description = "课程汇报、答辩、比赛展示PPT美化",
            location = "线上",
            schedule = "24小时内交付"
        )
    }

    private fun fallbackDebugService(): ServiceItem {
        return ServiceItem(
            title = "Java代码调试",
            category = "编程技术",
            provider = "陈同学",
            price = "25元/次",
            rating = "4.8",
            description = "帮助检查 Java / Kotlin 作业代码错误，并讲解修改思路",
            location = "线上 / 图书馆",
            schedule = "周二/四 18:00 后"
        )
    }

    private fun fallbackDeliveryService(): ServiceItem {
        return ServiceItem(
            title = "代取快递",
            category = "校园跑腿",
            provider = "王同学",
            price = "5元/次",
            rating = "4.7",
            description = "帮忙从快递站取件并送到宿舍楼下",
            location = "快递站",
            schedule = "当天 18:00 前"
        )
    }

    private fun bookService(service: ServiceItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("确认预约")
            .setMessage(
                """
            服务名称：${service.title}
            提供者：${service.provider}
            价格：${service.price}
            
            确定要预约这个服务吗？
            """.trimIndent()
            )
            .setNegativeButton("取消", null)
            .setPositiveButton("确认预约") { _, _ ->

                val newOrder = OrderItem(
                    title = service.title,
                    category = service.category,
                    provider = service.provider,
                    price = service.price,
                    status = "进行中",
                    description = service.description
                )

                AppDataStore.orders.add(0, newOrder)

                Toast.makeText(requireContext(), "预约成功，已加入我的订单", Toast.LENGTH_SHORT).show()

                (activity as? MainActivity)?.selectBottomTab(R.id.nav_orders)
            }
            .show()
    }
}