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

class ServicesFragment : Fragment() {
    private var lastToastMessage = ""
    private var lastToastTime = 0L
    private var currentToast: Toast? = null
    private var currentCategory = "全部"
    private var currentKeyword = ""

    private lateinit var serviceCards: List<ServiceCardBinding>
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
            updateBookingStateForAll()
            showSingleToast("已更新服务")
        }

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

        serviceCards.forEach { binding ->
            binding.card.setOnClickListener {
                showServiceDetail(binding.service)
            }

            binding.bookButton.setOnClickListener {
                bookService(binding.service)
            }
        }

        selectCategory("全部", chipAll)
        updateBookingStateForAll()
    }

    override fun onResume() {
        super.onResume()
        updateBookingStateForAll()
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

    private fun findService(keyword: String, fallback: ServiceItem): ServiceItem {
        return AppDataStore.services.firstOrNull {
            it.title.contains(keyword, ignoreCase = true) ||
                    it.category.contains(keyword, ignoreCase = true) ||
                    it.description.contains(keyword, ignoreCase = true)
        } ?: fallback
    }

    private fun fallbackMathService() = AppDataStore.services[0]
    private fun fallbackPptService() = AppDataStore.services[1]
    private fun fallbackDeliveryService() = AppDataStore.services[2]
    private fun fallbackDebugService() = AppDataStore.services[3]

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

        updateBookingStateForAll()

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

    private fun updateBookingStateForAll() {
        serviceCards.forEach { binding ->
            applyBookingState(binding)
        }
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