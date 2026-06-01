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
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class OrdersFragment : Fragment() {

    private lateinit var ordersAdapter: OrdersAdapter
    private var currentFilter = OrderFilter.ALL

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

        ordersAdapter = OrdersAdapter(
            onCardClick = { order ->
                showOrderDetail(order)
            },
            onPrimaryAction = { order, action ->
                handleOrderAction(order, action)
            },
            onSecondaryAction = { order, action ->
                handleOrderAction(order, action)
            }
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

        refreshLayout.setColorSchemeColors(
            ContextCompat.getColor(requireContext(), R.color.blue_primary)
        )
        refreshLayout.setOnRefreshListener {
            applyFilter(currentFilter, filters.keys, emptyText)
            refreshLayout.isRefreshing = false
            Toast.makeText(requireContext(), "已更新订单", Toast.LENGTH_SHORT).show()
        }

        buttonSchool.setOnClickListener {
            (activity as? MainActivity)?.selectBottomTab(R.id.nav_home)
        }

        buttonNotifications.setOnClickListener {
            showOrderNotifications()
        }

        applyFilter(OrderFilter.ALL, filters.keys, emptyText)
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

        val filtered = when (filter) {
            OrderFilter.ALL -> AppDataStore.orders.toList()
            else -> AppDataStore.orders.filter { it.status == filter.status }
        }

        ordersAdapter.submitList(filtered)
        emptyText.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
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

    private fun handleOrderAction(order: OrderItem, action: OrderAction) {
        when (action) {
            OrderAction.CANCEL -> {
                releaseBookedService(order)
                updateOrderStatus(order, "已取消")
                Toast.makeText(requireContext(), "已取消订单", Toast.LENGTH_SHORT).show()
            }
            OrderAction.PROGRESS -> {
                Toast.makeText(requireContext(), "正在查看进度", Toast.LENGTH_SHORT).show()
            }
            OrderAction.REORDER -> {
                Toast.makeText(requireContext(), "已创建相同需求", Toast.LENGTH_SHORT).show()
            }
            OrderAction.REVIEW -> {
                Toast.makeText(requireContext(), "进入评价", Toast.LENGTH_SHORT).show()
            }
            OrderAction.DELETE -> {
                releaseBookedService(order)
                AppDataStore.orders.remove(order)
                Toast.makeText(requireContext(), "已删除订单", Toast.LENGTH_SHORT).show()
            }
            OrderAction.CONTACT -> {
                Toast.makeText(requireContext(), "正在联系对方", Toast.LENGTH_SHORT).show()
            }
        }

        refreshOrders()
    }

    private fun updateOrderStatus(order: OrderItem, newStatus: String) {
        val index = AppDataStore.orders.indexOfFirst { it == order }
        if (index != -1) {
            AppDataStore.orders[index] = order.copy(status = newStatus)
        }
    }

    private fun releaseBookedService(order: OrderItem) {
        if (order.sourceServiceKey.isNotBlank()) {
            AppDataStore.bookedServiceKeys.remove(order.sourceServiceKey)
        }
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
        val pending = AppDataStore.orders.filter { it.status == "待接单" }
        val inProgress = AppDataStore.orders.filter { it.status == "进行中" }

        if (pending.isEmpty() && inProgress.isEmpty()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("订单通知")
                .setMessage("暂无新的订单动态")
                .setPositiveButton("关闭", null)
                .show()
            return
        }

        val lines = buildList {
            if (pending.isNotEmpty()) {
                add("待接单：")
                pending.take(3).forEach { add("• ${it.title}") }
            }
            if (inProgress.isNotEmpty()) {
                if (pending.isNotEmpty()) add("")
                add("进行中：")
                inProgress.take(3).forEach { add("• ${it.title}") }
            }
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("订单通知")
            .setMessage(lines.joinToString("\n"))
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

    private enum class OrderAction {
        CANCEL,
        PROGRESS,
        REORDER,
        REVIEW,
        DELETE,
        CONTACT
    }

    private class OrdersAdapter(
        private val onCardClick: (OrderItem) -> Unit,
        private val onPrimaryAction: (OrderItem, OrderAction) -> Unit,
        private val onSecondaryAction: (OrderItem, OrderAction) -> Unit
    ) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

        private val items = mutableListOf<OrderItem>()

        fun submitList(newItems: List<OrderItem>) {
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
            holder.bind(items[position], onCardClick, onPrimaryAction, onSecondaryAction)
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
                item: OrderItem,
                onCardClick: (OrderItem) -> Unit,
                onPrimaryAction: (OrderItem, OrderAction) -> Unit,
                onSecondaryAction: (OrderItem, OrderAction) -> Unit
            ) {
                statusBadge.text = item.status
                title.text = item.title
                category.text = item.category
                price.text = formatPrice(item.price)
                time.text = "刚刚"
                if (item.location.isBlank()) {
                    location.visibility = View.GONE
                } else {
                    location.visibility = View.VISIBLE
                    location.text = item.location
                }

                if (item.description.isBlank()) {
                    description.visibility = View.GONE
                } else {
                    description.visibility = View.VISIBLE
                    description.text = item.description
                }

                when (item.status) {
                    "待接单" -> {
                        applyBadgeStyle(R.drawable.bg_chip_warning, R.color.tertiary_amber)
                        primaryAction.text = "取消订单"
                        primaryAction.visibility = View.VISIBLE
                        secondaryAction.visibility = View.GONE
                        styleOutlinedAction(primaryAction, R.color.outline_variant, R.color.blue_on_surface)
                        primaryAction.setOnClickListener { onPrimaryAction(item, OrderAction.CANCEL) }
                    }
                    "进行中" -> {
                        applyBadgeStyle(R.drawable.bg_chip_info, R.color.info_blue)
                        primaryAction.text = "查看进度"
                        primaryAction.visibility = View.VISIBLE
                        secondaryAction.visibility = View.GONE
                        stylePrimaryAction(primaryAction)
                        primaryAction.setOnClickListener { onPrimaryAction(item, OrderAction.PROGRESS) }
                    }
                    "已完成" -> {
                        applyBadgeStyle(R.drawable.bg_chip_success, R.color.secondary_green)
                        secondaryAction.text = "再来一单"
                        primaryAction.text = "评价服务"
                        secondaryAction.visibility = View.VISIBLE
                        primaryAction.visibility = View.VISIBLE
                        styleOutlinedAction(secondaryAction, R.color.outline_variant, R.color.blue_on_surface)
                        styleOutlinedAction(primaryAction, R.color.secondary_green, R.color.secondary_green)
                        secondaryAction.setOnClickListener { onSecondaryAction(item, OrderAction.REORDER) }
                        primaryAction.setOnClickListener { onPrimaryAction(item, OrderAction.REVIEW) }
                    }
                    "已取消" -> {
                        applyBadgeStyle(R.drawable.bg_chip_neutral, R.color.outline)
                        primaryAction.text = "删除订单"
                        primaryAction.visibility = View.VISIBLE
                        secondaryAction.visibility = View.GONE
                        styleOutlinedAction(primaryAction, R.color.outline_variant, R.color.blue_on_surface)
                        primaryAction.setOnClickListener { onPrimaryAction(item, OrderAction.DELETE) }
                    }
                    else -> {
                        applyBadgeStyle(R.drawable.bg_chip_neutral, R.color.blue_on_surface)
                        primaryAction.text = "联系对方"
                        primaryAction.visibility = View.VISIBLE
                        secondaryAction.visibility = View.GONE
                        stylePrimaryAction(primaryAction)
                        primaryAction.setOnClickListener { onPrimaryAction(item, OrderAction.CONTACT) }
                    }
                }

                itemView.setOnClickListener { onCardClick(item) }
            }

            private fun formatPrice(raw: String): String {
                return when {
                    raw.contains("¥") -> raw
                    raw.contains("元") -> raw
                    else -> "¥$raw"
                }
            }

            private fun applyBadgeStyle(backgroundRes: Int, textColorRes: Int) {
                statusBadge.setBackgroundResource(backgroundRes)
                statusBadge.setTextColor(
                    ContextCompat.getColor(itemView.context, textColorRes)
                )
            }

            private fun stylePrimaryAction(button: MaterialButton) {
                val background = ContextCompat.getColor(itemView.context, R.color.blue_primary)
                val text = ContextCompat.getColor(itemView.context, R.color.blue_on_primary)
                button.backgroundTintList = ColorStateList.valueOf(background)
                button.setTextColor(text)
                button.strokeWidth = 0
            }

            private fun styleOutlinedAction(
                button: MaterialButton,
                strokeColorRes: Int,
                textColorRes: Int
            ) {
                val strokeColor = ContextCompat.getColor(itemView.context, strokeColorRes)
                val textColor = ContextCompat.getColor(itemView.context, textColorRes)
                val transparent = ContextCompat.getColor(itemView.context, android.R.color.transparent)
                button.backgroundTintList = ColorStateList.valueOf(transparent)
                button.strokeColor = ColorStateList.valueOf(strokeColor)
                button.strokeWidth = dp(1)
                button.setTextColor(textColor)
            }

            private fun dp(value: Int): Int {
                return (value * itemView.resources.displayMetrics.density).toInt()
            }
        }
    }
    private fun showOrderDetail(order: OrderItem) {
        val message = """
        类型：${order.category}
        提供者：${order.provider}
        价格：${order.price}
        状态：${order.status}
        地点：${order.location}
        
        订单说明：
        ${order.description}
    """.trimIndent()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(order.title)
            .setMessage(message)
            .setPositiveButton("关闭", null)
            .show()
    }
}
