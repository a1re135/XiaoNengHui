package com.example.xiaonenghui

data class ServiceItem(
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
    val title: String,
    val category: String,
    val location: String,
    val price: String,
    val status: String = "待接单",
    val description: String = "",
    val sourceServiceKey: String = ""
)

data class OrderItem(
    val title: String,
    val category: String,
    val provider: String,
    val price: String,
    val status: String = "待接单",
    val description: String = "",
    val location: String = "",
    val sourceServiceKey: String = ""
)

object AppDataStore {

    var currentRole = "需求方"
    var latestPostedService: ServiceItem? = null

    val orders = mutableListOf<OrderItem>()
    val bookedServiceKeys = mutableSetOf<String>()

    fun serviceKey(service: ServiceItem): String = "${service.title}::${service.provider}"

    fun isServiceBooked(service: ServiceItem): Boolean {
        return bookedServiceKeys.contains(serviceKey(service))
    }

    fun buildOrderFromService(service: ServiceItem): OrderItem {
        val key = serviceKey(service)
        return OrderItem(
            title = service.title,
            category = service.category,
            provider = service.provider,
            price = service.price,
            status = "待接单",
            description = service.description,
            location = service.location.ifBlank { "线上" },
            sourceServiceKey = key
        )
    }

    fun bookService(service: ServiceItem): OrderItem? {
        val key = serviceKey(service)
        if (bookedServiceKeys.contains(key)) {
            return null
        }

        val order = buildOrderFromService(service)
        orders.add(0, order)
        bookedServiceKeys.add(key)
        return order
    }

    fun releaseServiceBooking(order: OrderItem) {
        if (order.sourceServiceKey.isNotBlank()) {
            bookedServiceKeys.remove(order.sourceServiceKey)
        }
    }

    val services = mutableListOf(
        ServiceItem(
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
            title = "代取快递",
            category = "校园跑腿",
            location = "快递站",
            price = "8元",
            status = "待接单",
            description = "帮我从快递站取一个小包裹",
            sourceServiceKey = ""
        ),
        TaskItem(
            title = "PPT美化",
            category = "创意服务",
            location = "线上",
            price = "30元",
            status = "进行中",
            description = "帮我美化课程展示PPT",
            sourceServiceKey = ""
        )
    )
}