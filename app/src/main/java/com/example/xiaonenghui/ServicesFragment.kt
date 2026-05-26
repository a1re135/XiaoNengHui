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
import com.google.android.material.color.MaterialColors
import android.graphics.Typeface
import android.widget.LinearLayout

class ServicesFragment : Fragment() {
    private var lastToastMessage = ""
    private var lastToastTime = 0L
    private var currentToast: Toast? = null
    private var currentCategory = "全部"
    private var currentKeyword = ""

    private lateinit var staticServiceCards: List<ServiceCardBinding>
    private val dynamicServiceCards = mutableListOf<ServiceCardBinding>()
    private val serviceCards: List<ServiceCardBinding>
        get() = staticServiceCards + dynamicServiceCards

    private lateinit var chips: List<TextView>
    private lateinit var servicesListContainer: LinearLayout

    data class ServiceCardBinding(
        var service: ServiceItem,
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

        servicesListContainer = view.findViewById(R.id.services_list_container)

        val chipAll = view.findViewById<TextView>(R.id.chip_all)
        val chipTutoring = view.findViewById<TextView>(R.id.chip_tutoring)
        val chipCreative = view.findViewById<TextView>(R.id.chip_creative)
        val chipErrands = view.findViewById<TextView>(R.id.chip_errands)
        val chipProgramming = view.findViewById<TextView>(R.id.chip_programming)
        val chipOther = view.findViewById<TextView>(R.id.chip_other)

        chips = listOf(chipAll, chipTutoring, chipCreative, chipErrands, chipProgramming, chipOther)

        val latestPosted = AppDataStore.latestPostedService
        val overrideSlot = when {
            latestPosted == null -> null
            latestPosted.category.contains("创意") -> "ppt"
            latestPosted.category.contains("跑腿") -> "delivery"
            latestPosted.category.contains("编程") -> "debug"
            else -> "math"
        }

        val mathService = if (overrideSlot == "math") {
            latestPosted
        } else {
            findService("数学", fallbackMathService())
        } ?: fallbackMathService()

        val pptService = if (overrideSlot == "ppt") {
            latestPosted
        } else {
            findService("PPT", fallbackPptService())
        } ?: fallbackPptService()

        val debugService = if (overrideSlot == "debug") {
            latestPosted
        } else {
            findService("代码", fallbackDebugService())
        } ?: fallbackDebugService()

        val deliveryService = if (overrideSlot == "delivery") {
            latestPosted
        } else {
            findService("快递", fallbackDeliveryService())
        } ?: fallbackDeliveryService()

        staticServiceCards = listOf(
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
                searchInput.clearFocus()
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

        chipOther.setOnClickListener {
            selectCategory("其他", chipOther)
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
        refreshPostedServiceCards()
    }
    override fun onResume() {
        super.onResume()

        if (::servicesListContainer.isInitialized) {
            refreshPostedServiceCards()
        }
    }
    private fun submitSearch(rawKeyword: String) {
        val keyword = rawKeyword.trim()
        currentKeyword = keyword
        val visibleCount = renderServices()
        if (keyword.isEmpty()) {
            return
        }
        if (visibleCount == 0) {
            showSingleToast("未找到相关服务")
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
        val lines = mutableListOf(
            "服务类型：${service.category}",
            "提供者：${service.provider}",
            "价格：${service.price}",
            "评分：${service.rating}",
            "地点：${service.location}",
            "可预约时间：${service.schedule}",
            "",
            "服务描述：",
            service.description
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(service.title)
            .setMessage(lines.joinToString("\n"))
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
    private fun showSingleToast(message: String) {
        val now = System.currentTimeMillis()

        if (message == lastToastMessage && now - lastToastTime < 800) {
            return
        }

        lastToastMessage = message
        lastToastTime = now

        currentToast?.cancel()
        currentToast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }
    private fun refreshPostedServiceCards() {
        val oldDynamicViews = mutableListOf<View>()

        for (i in 0 until servicesListContainer.childCount) {
            val child = servicesListContainer.getChildAt(i)
            if (child.tag == "posted_service_card") {
                oldDynamicViews.add(child)
            }
        }

        oldDynamicViews.forEach {
            servicesListContainer.removeView(it)
        }

        dynamicServiceCards.clear()

        val postedServices = AppDataStore.services.filter {
            it.provider == "发布者" || it.rating == "新"
        }

        postedServices.reversed().forEach { service ->
            val card = createPostedServiceCard(service)
            servicesListContainer.addView(card, 0)
        }

        renderServices()
    }
    private fun createPostedServiceCard(service: ServiceItem): MaterialCardView {
        val categoryStyle = resolveCategoryStyle(service.category)

        val card = MaterialCardView(requireContext()).apply {
            tag = "posted_service_card"
            radius = dp(16).toFloat()
            cardElevation = dp(4).toFloat()
            strokeWidth = dp(1)
            setStrokeColor(ContextCompat.getColor(requireContext(), R.color.outline_variant))
            setCardBackgroundColor(MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurface))

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 0, 0, dp(12))
            layoutParams = params
        }

        val content = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(14))
        }

        val titleRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val icon = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(dp(40), dp(40))
            background = ContextCompat.getDrawable(requireContext(), categoryStyle.iconBackground)
            gravity = android.view.Gravity.CENTER
            text = categoryStyle.iconText
            setTextColor(ContextCompat.getColor(requireContext(), categoryStyle.iconTextColor))
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
        }

        val titleColumn = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(dp(12), 0, 0, 0)
            params.weight = 1f
            layoutParams = params
        }

        val title = TextView(requireContext()).apply {
            text = service.title
            setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_on_surface))
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
        }

        val category = TextView(requireContext()).apply {
            text = service.category
            background = ContextCompat.getDrawable(requireContext(), categoryStyle.tagBackground)
            setTextColor(ContextCompat.getColor(requireContext(), categoryStyle.tagTextColor))
            textSize = 10f
            setPadding(dp(4), dp(4), dp(4), dp(4))

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, dp(6), 0, 0)
            layoutParams = params
        }

        titleColumn.addView(title)
        titleColumn.addView(category)

        val rating = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_chip_warning)
            gravity = android.view.Gravity.CENTER
            setPadding(dp(8), dp(4), dp(8), dp(4))
        }

        val ratingIcon = TextView(requireContext()).apply {
            text = "★"
            setTextColor(ContextCompat.getColor(requireContext(), R.color.tertiary_amber))
            textSize = 12f
        }

        val ratingValue = TextView(requireContext()).apply {
            text = service.rating
            setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_on_surface))
            textSize = 12f
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(dp(4), 0, 0, 0)
            layoutParams = params
        }

        rating.addView(ratingIcon)
        rating.addView(ratingValue)

        titleRow.addView(icon)
        titleRow.addView(titleColumn)
        titleRow.addView(rating)

        val description = TextView(requireContext()).apply {
            text = service.description
            setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_on_surface))
            textSize = 13f

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, dp(10), 0, 0)
            layoutParams = params
        }

        val bottomRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, dp(12), 0, 0)
            layoutParams = params
        }

        val providerIcon = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(dp(24), dp(24))
            background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_circle_secondary)
            gravity = android.view.Gravity.CENTER
            text = service.provider.take(1)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary_green))
            textSize = 12f
            setTypeface(null, Typeface.BOLD)
        }

        val providerName = TextView(requireContext()).apply {
            text = service.provider
            setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_on_surface))
            textSize = 12f
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(dp(6), 0, 0, 0)
            layoutParams = params
        }

        val price = TextView(requireContext()).apply {
            text = service.price
            setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_primary))
            textSize = 13f
            setTypeface(null, Typeface.BOLD)
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.setMargins(dp(8), 0, 0, 0)
            params.weight = 1f
            layoutParams = params
        }

        val bookButton = MaterialButton(requireContext()).apply {
            text = getString(R.string.services_book_now)
            cornerRadius = dp(12)
        }

        bottomRow.addView(providerIcon)
        bottomRow.addView(providerName)
        bottomRow.addView(price)
        bottomRow.addView(bookButton)

        content.addView(titleRow)
        content.addView(description)
        content.addView(bottomRow)

        card.addView(content)

        val binding = ServiceCardBinding(
            service = service,
            card = card,
            bookButton = bookButton
        )

        dynamicServiceCards.add(binding)

        card.setOnClickListener {
            showServiceDetail(service)
        }

        bookButton.setOnClickListener {
            bookService(service)
        }

        return card
    }

    private data class CategoryStyle(
        val iconText: String,
        val iconBackground: Int,
        val iconTextColor: Int,
        val tagBackground: Int,
        val tagTextColor: Int
    )

    private fun resolveCategoryStyle(category: String): CategoryStyle {
        return when {
            category.contains("创意") -> CategoryStyle(
                iconText = "创",
                iconBackground = R.drawable.bg_circle_secondary,
                iconTextColor = R.color.secondary_green,
                tagBackground = R.drawable.bg_tag_secondary,
                tagTextColor = R.color.secondary_green
            )
            category.contains("跑腿") -> CategoryStyle(
                iconText = "跑",
                iconBackground = R.drawable.bg_circle_tertiary,
                iconTextColor = R.color.tertiary_amber,
                tagBackground = R.drawable.bg_tag_tertiary,
                tagTextColor = R.color.tertiary_amber
            )
            category.contains("编程") -> CategoryStyle(
                iconText = "码",
                iconBackground = R.drawable.bg_circle_neutral,
                iconTextColor = R.color.blue_on_surface,
                tagBackground = R.drawable.bg_tag_tertiary,
                tagTextColor = R.color.tertiary_amber
            )
            else -> CategoryStyle(
                iconText = "辅",
                iconBackground = R.drawable.bg_circle_primary,
                iconTextColor = R.color.blue_primary,
                tagBackground = R.drawable.bg_tag_primary,
                tagTextColor = R.color.blue_primary
            )
        }
    }
    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}