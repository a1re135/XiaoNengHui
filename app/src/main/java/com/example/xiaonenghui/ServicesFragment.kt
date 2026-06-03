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
import android.widget.LinearLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.graphics.Typeface
import com.google.android.material.color.MaterialColors

class ServicesFragment : Fragment() {
    private var lastToastMessage = ""
    private var lastToastTime = 0L
    private var currentToast: Toast? = null
    private var currentCategory = "全部"
    private var currentKeyword = ""

    private val serviceCardBindings = mutableListOf<ServiceCardBinding>()
    private lateinit var chips: List<TextView>
    private lateinit var servicesListContainer: LinearLayout

    data class ServiceCardBinding(
        var service: ServiceItem,
        val card: View,
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
        val notificationButton = view.findViewById<View>(R.id.button_services_notifications)
        val schoolButton = view.findViewById<View>(R.id.button_services_school)
        val refreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.services_refresh)

        servicesListContainer = view.findViewById(R.id.services_list_container)

        val chipAll = view.findViewById<TextView>(R.id.chip_all)
        val chipTutoring = view.findViewById<TextView>(R.id.chip_tutoring)
        val chipCreative = view.findViewById<TextView>(R.id.chip_creative)
        val chipErrands = view.findViewById<TextView>(R.id.chip_errands)
        val chipProgramming = view.findViewById<TextView>(R.id.chip_programming)
        val chipOther = view.findViewById<TextView>(R.id.chip_other)

        chips = listOf(chipAll, chipTutoring, chipCreative, chipErrands, chipProgramming, chipOther)

        refreshLayout.setColorSchemeColors(
            ContextCompat.getColor(requireContext(), R.color.blue_primary)
        )
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            refreshServiceList()
            showSingleToast("已更新服务")
        }

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

        schoolButton.setOnClickListener {
            (activity as? MainActivity)?.selectBottomTab(R.id.nav_home)
        }

        chipAll.setOnClickListener { selectCategory("全部", chipAll) }
        chipTutoring.setOnClickListener { selectCategory("技能辅导", chipTutoring) }
        chipCreative.setOnClickListener { selectCategory("创意服务", chipCreative) }
        chipErrands.setOnClickListener { selectCategory("校园跑腿", chipErrands) }
        chipProgramming.setOnClickListener { selectCategory("编程技术", chipProgramming) }
        chipOther.setOnClickListener { selectCategory("其他", chipOther) }

        refreshServiceList()
        selectCategory("全部", chipAll)
    }

    override fun onResume() {
        super.onResume()
        refreshServiceList()
    }

    private fun refreshServiceList() {
        if (!::servicesListContainer.isInitialized) return
        
        servicesListContainer.removeAllViews()
        serviceCardBindings.clear()

        AppDataStore.services.forEach { service ->
            val cardBinding = createServiceCard(service)
            servicesListContainer.addView(cardBinding.card)
            serviceCardBindings.add(cardBinding)
        }
        
        renderServices()
    }

    private fun createServiceCard(service: ServiceItem): ServiceCardBinding {
        val categoryStyle = resolveCategoryStyle(service.category)

        val card = MaterialCardView(requireContext()).apply {
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

        card.setOnClickListener {
            showServiceDetail(service)
        }

        bookButton.setOnClickListener {
            bookService(service)
        }

        applyBookingState(binding)

        return binding
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

    private fun submitSearch(rawKeyword: String) {
        val keyword = rawKeyword.trim()
        currentKeyword = keyword
        val visibleCount = renderServices()
        if (keyword.isNotEmpty() && visibleCount == 0) {
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

        serviceCardBindings.forEach { binding ->
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

        val alreadyBooked = AppDataStore.isServiceBooked(service)
        val positiveLabel = if (alreadyBooked) getString(R.string.home_booked_label) else getString(R.string.home_book_now)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(service.title)
            .setMessage(lines.joinToString("\n"))
            .setNegativeButton("关闭", null)
            .setPositiveButton(positiveLabel) { _, _ ->
                if (!alreadyBooked) {
                    bookService(service)
                }
            }
            .show()
            .apply {
                if (alreadyBooked) {
                    getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)?.isEnabled = false
                }
            }
    }

    private fun showNotifications() {
        val message = AppDataStore.getNotificationMessage()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("通知")
            .setMessage(message)
            .setPositiveButton("关闭", null)
            .show()
    }

    private fun bookService(service: ServiceItem) {
        if (AppDataStore.isServiceBooked(service)) {
            showSingleToast("该服务已预约")
            return
        }

        val bookedOrder = AppDataStore.bookService(service)
        if (bookedOrder == null) {
            showSingleToast("该服务已预约")
            return
        }

        refreshServiceList()

        Toast.makeText(requireContext(), "预约成功，已加入我的订单", Toast.LENGTH_SHORT).show()

        (activity as? MainActivity)?.selectBottomTab(R.id.nav_orders)
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

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun applyBookingState(binding: ServiceCardBinding) {
        val booked = AppDataStore.isServiceBooked(binding.service)
        val button = binding.bookButton

        if (booked) {
            button.text = "已预约"
            button.isEnabled = false
            button.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.surface_container_high)
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.outline))
            button.strokeWidth = dp(1)
            button.strokeColor = ContextCompat.getColorStateList(requireContext(), R.color.outline_variant)
        } else {
            button.text = getString(R.string.services_book_now)
            button.isEnabled = true
            button.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue_primary)
            button.strokeWidth = 0
            button.strokeColor = null
            button.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_on_primary))
        }
    }
}
