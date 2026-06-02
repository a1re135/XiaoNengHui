package com.example.xiaonenghui

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class OrdersFragment : Fragment() {

    private lateinit var ordersAdapter: OrdersAdapter
    private var currentFilter = OrderFilter.ALL
    private var currentMode = OrderMode.REQUESTER

    private enum class OrderMode {
        REQUESTER, PROVIDER
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ordersList = view.findViewById<RecyclerView>(R.id.orders_list)
        val emptyText = view.findViewById<TextView>(R.id.orders_empty_text)
        val refreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.orders_refresh)
        val buttonSchool = view.findViewById<View>(R.id.button_orders_school)
        val buttonNotifications = view.findViewById<View>(R.id.button_orders_notifications)
        val modeToggleGroup = view.findViewById<MaterialButtonToggleGroup>(R.id.order_mode_toggle_group)

        val btnRequester = view.findViewById<MaterialButton>(R.id.btn_mode_requester)
        val btnProvider = view.findViewById<MaterialButton>(R.id.btn_mode_provider)

        ordersAdapter = OrdersAdapter(
            mode = currentMode,
            onCardClick = { item -> showOrderDetail(item) },
            onAction = { item, action -> handleAction(item, action) }
        )

        ordersList.layoutManager = LinearLayoutManager(requireContext())
        ordersList.adapter = ordersAdapter

        val filterAll = view.findViewById<MaterialButton>(R.id.filter_all)
        val filterPending = view.findViewById<MaterialButton>(R.id.filter_pending)
        val filterInProgress = view.findViewById<MaterialButton>(R.id.filter_in_progress)
        val filterCompleted = view.findViewById<MaterialButton>(R.id.filter_completed)
        val filterCancelled = view.findViewById<MaterialButton>(R.id.filter_cancelled)

        val filters = linkedMapOf(
            filterAll to OrderFilter.ALL,
            filterPending to OrderFilter.PENDING,
            filterInProgress to OrderFilter.IN_PROGRESS,
            filterCompleted to OrderFilter.COMPLETED,
            filterCancelled to OrderFilter.CANCELLED
        )

        filters.forEach { (button, filter) ->
            button.setOnClickListener { applyFilter(filter, filters.keys, emptyText) }
        }

        modeToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            currentMode = when (checkedId) {
                R.id.btn_mode_provider -> OrderMode.PROVIDER
                else -> OrderMode.REQUESTER
            }
            updateModeButtons(btnRequester, btnProvider)
            applyFilter(currentFilter, filters.keys, emptyText)
        }

        refreshLayout.setColorSchemeColors(
            ContextCompat.getColor(requireContext(), R.color.blue_primary)
        )
        refreshLayout.setOnRefreshListener {
            applyFilter(currentFilter, filters.keys, emptyText)
            refreshLayout.isRefreshing = false
            Toast.makeText(requireContext(), "已更新数据", Toast.LENGTH_SHORT).show()
        }

        buttonSchool.setOnClickListener {
            (activity as? MainActivity)?.selectBottomTab(R.id.nav_home)
        }

        buttonNotifications.setOnClickListener {
            showOrderNotifications()
        }

        updateModeButtons(btnRequester, btnProvider)
        modeToggleGroup.check(if (currentMode == OrderMode.REQUESTER) R.id.btn_mode_requester else R.id.btn_mode_provider)
        applyFilter(OrderFilter.ALL, filters.keys, emptyText)
    }

    private fun updateModeButtons(requester: MaterialButton, provider: MaterialButton) {
        requester.isChecked = currentMode == OrderMode.REQUESTER
        provider.isChecked = currentMode == OrderMode.PROVIDER
    }

    override fun onResume() {
        super.onResume()
        refreshOrders()
    }

    private fun applyFilter(
        filter: OrderFilter,
        buttons: Collection<MaterialButton>,
        emptyText: TextView
    ) {
        currentFilter = filter
        updateFilterButtons(filter, buttons)

        val rawList: List<Any> = if (currentMode == OrderMode.REQUESTER) {
            AppDataStore.orders
        } else {
            AppDataStore.tasks
        }

        val filtered = rawList.filter { item ->
            val status = when (item) {
                is OrderItem -> item.status
                is TaskItem -> item.status
                else -> ""
            }
            filter == OrderFilter.ALL || status == filter.status
        }

        ordersAdapter.updateMode(currentMode)
        ordersAdapter.submitList(filtered)
        emptyText.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        emptyText.text = if (currentMode == OrderMode.REQUESTER) "暂无订单" else "暂无发布或可接任务"
    }

    private fun updateFilterButtons(filter: OrderFilter, buttons: Collection<MaterialButton>) {
        val activeBackground = ContextCompat.getColor(requireContext(), R.color.blue_primary_container)
        val activeText = ContextCompat.getColor(requireContext(), R.color.blue_on_primary_container)
        val inactiveBackground = ContextCompat.getColor(requireContext(), R.color.surface_container_high)
        val inactiveText = ContextCompat.getColor(requireContext(), R.color.blue_on_surface)
        val inactiveStroke = ContextCompat.getColor(requireContext(), R.color.outline_variant)

        buttons.forEach { button ->
            val isActive = when (filter) {
                OrderFilter.ALL -> button.id == R.id.filter_all
                OrderFilter.PENDING -> button.id == R.id.filter_pending
                OrderFilter.IN_PROGRESS -> button.id == R.id.filter_in_progress
                OrderFilter.COMPLETED -> button.id == R.id.filter_completed
                OrderFilter.CANCELLED -> button.id == R.id.filter_cancelled
            }

            val background = if (isActive) activeBackground else inactiveBackground
            val textColor = if (isActive) activeText else inactiveText
            button.backgroundTintList = ColorStateList.valueOf(background)
            button.setTextColor(textColor)
            button.strokeColor = ColorStateList.valueOf(inactiveStroke)
            button.strokeWidth = if (isActive) 0 else dp(1)
            button.setTypeface(null, if (isActive) Typeface.BOLD else Typeface.NORMAL)
        }
    }

    private fun handleAction(item: Any, action: String) {
        when (action) {
            "CANCEL" -> {
                if (item is OrderItem) {
                    AppDataStore.releaseServiceBooking(item)
                    item.status = "已取消"
                    Toast.makeText(requireContext(), "已取消订单", Toast.LENGTH_SHORT).show()
                }
            }
            "DELETE" -> {
                if (item is OrderItem) {
                    AppDataStore.releaseServiceBooking(item)
                    AppDataStore.orders.remove(item)
                    Toast.makeText(requireContext(), "已删除订单", Toast.LENGTH_SHORT).show()
                }
            }
            "ACCEPT" -> {
                if (item is TaskItem) {
                    item.status = "进行中"
                    Toast.makeText(requireContext(), "已接单，任务进行中", Toast.LENGTH_SHORT).show()
                }
            }
            "COMPLETE" -> {
                if (item is TaskItem) {
                    item.status = "已完成"
                    Toast.makeText(requireContext(), "任务已完成", Toast.LENGTH_SHORT).show()
                }
            }
            "REVIEW" -> {
                showRatingDialog(item)
            }
        }
        refreshOrders()
    }

    private fun showRatingDialog(item: Any) {
        val ratings = arrayOf("1星", "2星", "3星", "4星", "5星")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("评价服务")
            .setItems(ratings) { _, which ->
                val ratingValue = which + 1
                if (item is OrderItem) item.rating = ratingValue
                if (item is TaskItem) item.rating = ratingValue
                Toast.makeText(requireContext(), "感谢您的评价：$ratingValue 星", Toast.LENGTH_SHORT).show()
                refreshOrders()
            }
            .show()
    }

    private fun refreshOrders() {
        view?.findViewById<TextView>(R.id.orders_empty_text)?.let { emptyText ->
            val buttons = listOfNotNull(
                view?.findViewById(R.id.filter_all),
                view?.findViewById(R.id.filter_pending),
                view?.findViewById(R.id.filter_in_progress),
                view?.findViewById(R.id.filter_completed),
                view?.findViewById(R.id.filter_cancelled)
            )
            applyFilter(currentFilter, buttons.filterIsInstance<MaterialButton>(), emptyText)
        }
    }

    private fun showOrderNotifications() {
        val message = AppDataStore.getNotificationMessage()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("通知")
            .setMessage(message)
            .setPositiveButton("关闭", null)
            .show()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private enum class OrderFilter(val status: String?) {
        ALL(null),
        PENDING("待接单"),
        IN_PROGRESS("进行中"),
        COMPLETED("已完成"),
        CANCELLED("已取消")
    }

    private class OrdersAdapter(
        private var mode: OrderMode,
        private val onCardClick: (Any) -> Unit,
        private val onAction: (Any, String) -> Unit
    ) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

        private val items = mutableListOf<Any>()

        fun updateMode(newMode: OrderMode) {
            mode = newMode
        }

        fun submitList(newItems: List<Any>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_card, parent, false)
            return OrderViewHolder(view)
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            holder.bind(items[position], mode, onCardClick, onAction)
        }

        override fun getItemCount(): Int = items.size

        class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val statusBadge = itemView.findViewById<TextView>(R.id.order_status_badge)
            private val time = itemView.findViewById<TextView>(R.id.order_time)
            private val title = itemView.findViewById<TextView>(R.id.order_title)
            private val category = itemView.findViewById<TextView>(R.id.order_category_chip)
            private val price = itemView.findViewById<TextView>(R.id.order_price)
            private val location = itemView.findViewById<TextView>(R.id.order_location)
            private val description = itemView.findViewById<TextView>(R.id.order_description)
            private val primaryAction = itemView.findViewById<MaterialButton>(R.id.order_action_primary)
            private val secondaryAction = itemView.findViewById<MaterialButton>(R.id.order_action_secondary)

            fun bind(
                item: Any,
                mode: OrderMode,
                onCardClick: (Any) -> Unit,
                onAction: (Any, String) -> Unit
            ) {
                val itemTitle: String
                val itemCategory: String
                val itemPrice: String
                val itemStatus: String
                val itemDescription: String
                val itemLocation: String
                val itemRating: Int?

                if (item is OrderItem) {
                    itemTitle = item.title
                    itemCategory = item.category
                    itemPrice = item.price
                    itemStatus = item.status
                    itemDescription = item.description
                    itemLocation = item.location
                    itemRating = item.rating
                } else if (item is TaskItem) {
                    itemTitle = item.title
                    itemCategory = item.category
                    itemPrice = item.price
                    itemStatus = item.status
                    itemDescription = item.description
                    itemLocation = item.location
                    itemRating = item.rating
                } else return

                statusBadge.text = itemStatus
                title.text = itemTitle
                category.text = itemCategory
                price.text = if (itemPrice.startsWith("¥") || itemPrice.contains("元")) itemPrice else "¥$itemPrice"
                time.text = if (itemRating != null) "评分：$itemRating 星" else "刚刚"
                
                if (itemLocation.isBlank()) {
                    location.visibility = View.GONE
                } else {
                    location.visibility = View.VISIBLE
                    location.text = itemLocation
                }

                if (itemDescription.isBlank()) {
                    description.visibility = View.GONE
                } else {
                    description.visibility = View.VISIBLE
                    description.text = itemDescription
                }

                primaryAction.visibility = View.VISIBLE
                secondaryAction.visibility = View.GONE

                if (mode == OrderMode.REQUESTER) {
                    when (itemStatus) {
                        "待接单" -> {
                            applyBadgeStyle(R.drawable.bg_chip_warning, R.color.tertiary_amber)
                            primaryAction.text = "取消订单"
                            styleOutlinedAction(primaryAction, R.color.outline_variant, R.color.blue_on_surface)
                            primaryAction.setOnClickListener { onAction(item, "CANCEL") }
                        }
                        "进行中" -> {
                            applyBadgeStyle(R.drawable.bg_chip_info, R.color.info_blue)
                            primaryAction.text = "查看进度"
                            stylePrimaryAction(primaryAction)
                            primaryAction.setOnClickListener { Toast.makeText(itemView.context, "正在追踪进度", Toast.LENGTH_SHORT).show() }
                        }
                        "已完成" -> {
                            applyBadgeStyle(R.drawable.bg_chip_success, R.color.secondary_green)
                            primaryAction.text = if (itemRating == null) "评价服务" else "已评价"
                            primaryAction.isEnabled = itemRating == null
                            styleOutlinedAction(primaryAction, R.color.secondary_green, R.color.secondary_green)
                            primaryAction.setOnClickListener { onAction(item, "REVIEW") }
                        }
                        "已取消" -> {
                            applyBadgeStyle(R.drawable.bg_chip_neutral, R.color.outline)
                            primaryAction.text = "删除订单"
                            styleOutlinedAction(primaryAction, R.color.outline_variant, R.color.blue_on_surface)
                            primaryAction.setOnClickListener { onAction(item, "DELETE") }
                        }
                    }
                } else {
                    // PROVIDER mode
                    when (itemStatus) {
                        "待接单" -> {
                            applyBadgeStyle(R.drawable.bg_chip_warning, R.color.tertiary_amber)
                            primaryAction.text = "接单"
                            stylePrimaryAction(primaryAction)
                            primaryAction.setOnClickListener { onAction(item, "ACCEPT") }
                        }
                        "进行中" -> {
                            applyBadgeStyle(R.drawable.bg_chip_info, R.color.info_blue)
                            primaryAction.text = "标记完成"
                            stylePrimaryAction(primaryAction)
                            primaryAction.setOnClickListener { onAction(item, "COMPLETE") }
                        }
                        "已完成" -> {
                            applyBadgeStyle(R.drawable.bg_chip_success, R.color.secondary_green)
                            primaryAction.text = if (itemRating != null) "评分：$itemRating 星" else "等待评价"
                            primaryAction.isEnabled = false
                            styleOutlinedAction(primaryAction, R.color.outline_variant, R.color.outline)
                        }
                        else -> {
                            primaryAction.visibility = View.GONE
                        }
                    }
                }

                itemView.setOnClickListener { onCardClick(item) }
            }

            private fun applyBadgeStyle(backgroundRes: Int, textColorRes: Int) {
                statusBadge.setBackgroundResource(backgroundRes)
                statusBadge.setTextColor(ContextCompat.getColor(itemView.context, textColorRes))
            }

            private fun stylePrimaryAction(button: MaterialButton) {
                button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.blue_primary))
                button.setTextColor(ContextCompat.getColor(itemView.context, R.color.blue_on_primary))
                button.strokeWidth = 0
            }

            private fun styleOutlinedAction(button: MaterialButton, strokeColorRes: Int, textColorRes: Int) {
                button.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, android.R.color.transparent))
                button.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, strokeColorRes))
                button.strokeWidth = (1 * itemView.resources.displayMetrics.density).toInt()
                button.setTextColor(ContextCompat.getColor(itemView.context, textColorRes))
            }
        }
    }

    private fun showOrderDetail(item: Any) {
        val title: String
        val details = mutableListOf<String>()

        if (item is OrderItem) {
            title = item.title
            details.add("类型：${item.category}")
            details.add("提供者：${item.provider}")
            details.add("价格：${item.price}")
            details.add("状态：${item.status}")
            details.add("地点：${item.location}")
            details.add("\n描述：${item.description}")
        } else if (item is TaskItem) {
            title = item.title
            details.add("类型：${item.category}")
            details.add("价格：${item.price}")
            details.add("状态：${item.status}")
            details.add("地点：${item.location}")
            details.add("\n描述：${item.description}")
        } else return

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(details.joinToString("\n"))
            .setPositiveButton("关闭", null)
            .show()
    }
}