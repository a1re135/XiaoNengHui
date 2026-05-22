package com.example.xiaonenghui

data class ServiceItem(
    val title: String,
    val category: String,
    val provider: String,
    val price: String,
    val rating: String
)

data class TaskItem(
    val title: String,
    val category: String,
    val location: String,
    val price: String,
    val status: String = "待接单",
    val description: String = ""
)

object AppDataStore {

    var currentRole = "需求方"

    val services = mutableListOf(
        ServiceItem("高等数学辅导", "技能辅导", "张同学", "20元/小时", "4.9"),
        ServiceItem("PPT制作", "创意服务", "李同学", "15元/份", "4.8"),
        ServiceItem("代取快递", "校园跑腿", "王同学", "5元/次", "4.7")
    )

    val tasks = mutableListOf(
        TaskItem(
            title = "代取快递",
            category = "校园跑腿",
            location = "快递站",
            price = "8元",
            status = "待接单",
            description = "帮我从快递站取一个小包裹"
        ),
        TaskItem(
            title = "PPT美化",
            category = "创意服务",
            location = "线上",
            price = "30元",
            status = "进行中",
            description = "帮我美化课程展示PPT"
        )
    )
}