package com.example.xiaonenghui

import java.util.UUID

data class ServiceItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val provider: String,
    val price: String,
    val rating: String,
    val description: String = "",
    val location: String = "",
    val schedule: String = ""
)

data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val location: String,
    val price: String,
    var status: String = "待接单",
    val description: String = "",
    val time: String = "",
    val sourceServiceKey: String = "",
    var rating: Int? = null,
    val isPostedByMe: Boolean = false
)

data class OrderItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String,
    val provider: String,
    val price: String,
    var status: String = "待接单",
    val description: String = "",
    val location: String = "",
    val sourceServiceId: String = "",
    var rating: Int? = null
)

object AppDataStore {

    var currentRole = "需求方"

    val orders = mutableListOf<OrderItem>()
    val bookedServiceIds = mutableSetOf<String>()
    val bookedTaskIds = mutableSetOf<String>()

    fun isServiceBooked(service: ServiceItem): Boolean {
        return bookedServiceIds.contains(service.id) || bookedTaskIds.contains(service.id)
    }

    fun isTaskBooked(task: TaskItem): Boolean {
        return bookedTaskIds.contains(task.id) || bookedServiceIds.contains(task.id)
    }

    fun bookService(service: ServiceItem): OrderItem? {
        if (bookedServiceIds.contains(service.id)) {
            return null
        }

        val order = OrderItem(
            title = service.title,
            category = service.category,
            provider = service.provider,
            price = service.price,
            status = "待接单",
            description = service.description,
            location = service.location.ifBlank { "线上" },
            sourceServiceId = service.id
        )
        orders.add(0, order)
        bookedServiceIds.add(service.id)
        return order
    }

    fun bookTask(task: TaskItem): OrderItem? {
        if (bookedTaskIds.contains(task.id)) {
            return null
        }

        val order = OrderItem(
            title = task.title,
            category = task.category,
            provider = "发布者", // Originally posted by someone else, but in this simplified model...
            price = task.price,
            status = "待接单",
            description = task.description,
            location = task.location,
            sourceServiceId = task.id
        )
        orders.add(0, order)
        bookedTaskIds.add(task.id)
        task.status = "进行中"
        return order
    }

    fun releaseServiceBooking(order: OrderItem) {
        if (order.sourceServiceId.isNotBlank()) {
            bookedServiceIds.remove(order.sourceServiceId)
            bookedTaskIds.remove(order.sourceServiceId)
        }
    }

    fun getNotificationMessage(): String {
        val pendingCount = tasks.count { it.status == "待接单" }
        val inProgressCount = tasks.count { it.status == "进行中" }

        return when {
            pendingCount > 0 && inProgressCount > 0 -> 
                "你有 $pendingCount 个任务待接单，$inProgressCount 个任务进行中"
            pendingCount > 0 -> 
                "你有 $pendingCount 个任务待接单"
            inProgressCount > 0 -> 
                "你有 $inProgressCount 个任务尚未完成"
            else -> "暂无新的通知"
        }
    }

    val services = mutableListOf(
        ServiceItem(
            id = "service_math_1",
            title = "高等数学辅导",
            category = "技能辅导",
            provider = "张同学",
            price = "20元/小时",
            rating = "4.9",
            description = "一对一讲解重点与作业辅导",
            location = "图书馆自习室",
            schedule = "周一/三晚 19:00"
        ),
        ServiceItem(
            id = "service_ppt_1",
            title = "PPT制作",
            category = "创意服务",
            provider = "李同学",
            price = "15元/份",
            rating = "4.8",
            description = "课程汇报/竞赛答辩PPT美化",
            location = "线上",
            schedule = "24小时内交付"
        ),
        ServiceItem(
            id = "service_delivery_1",
            title = "代取快递",
            category = "校园跑腿",
            provider = "王同学",
            price = "5元/次",
            rating = "4.7",
            description = "快速代取，校内送达",
            location = "快递站",
            schedule = "当天 18:00 前"
        ),
        ServiceItem(
            id = "service_java_1",
            title = "Java代码调试",
            category = "编程技术",
            provider = "陈同学",
            price = "25元/次",
            rating = "4.8",
            description = "帮助检查 Java / Kotlin 作业代码错误，并讲解修改思路",
            location = "线上 / 图书馆",
            schedule = "周二/四 18:00 后"
        )
    )

    val tasks = mutableListOf(
        TaskItem(
            id = "task_delivery_1",
            title = "代取快递",
            category = "校园跑腿",
            location = "快递站",
            price = "8元",
            status = "待接单",
            description = "帮我从快递站取一个小包裹",
            isPostedByMe = false
        ),
        TaskItem(
            id = "task_ppt_1",
            title = "PPT美化",
            category = "创意服务",
            location = "线上",
            price = "30元",
            status = "进行中",
            description = "帮我美化课程展示PPT",
            isPostedByMe = false
        )
    )
}