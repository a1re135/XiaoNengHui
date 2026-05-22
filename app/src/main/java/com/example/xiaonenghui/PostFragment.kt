package com.example.xiaonenghui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText

class PostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputTitle = view.findViewById<TextInputEditText>(R.id.input_task_title)
        val inputCategory = view.findViewById<TextInputEditText>(R.id.input_task_category)
        val inputPrice = view.findViewById<TextInputEditText>(R.id.input_task_price)
        val inputLocation = view.findViewById<TextInputEditText>(R.id.input_task_location)
        val inputDescription = view.findViewById<TextInputEditText>(R.id.input_task_description)
        val publishButton = view.findViewById<Button>(R.id.button_publish_task)

        publishButton.setOnClickListener {
            val title = inputTitle.text.toString().trim()
            val category = inputCategory.text.toString().trim()
            val price = inputPrice.text.toString().trim()
            val location = inputLocation.text.toString().trim()
            val description = inputDescription.text.toString().trim()

            if (title.isEmpty()) {
                inputTitle.error = "任务标题不能为空"
                inputTitle.requestFocus()
                return@setOnClickListener
            }

            if (price.isEmpty()) {
                inputPrice.error = "预算金额不能为空"
                inputPrice.requestFocus()
                return@setOnClickListener
            }

            if (description.isEmpty()) {
                inputDescription.error = "任务描述不能为空"
                inputDescription.requestFocus()
                return@setOnClickListener
            }

            val newTask = TaskItem(
                title = title,
                category = if (category.isEmpty()) "其他" else category,
                location = if (location.isEmpty()) "未填写地点" else location,
                price = "${price}元",
                description = description,
                status = "待接单"
            )

            AppDataStore.tasks.add(0, newTask)

            Toast.makeText(requireContext(), "任务发布成功", Toast.LENGTH_SHORT).show()

            inputTitle.text?.clear()
            inputPrice.text?.clear()
            inputLocation.text?.clear()
            inputDescription.text?.clear()

            (activity as? MainActivity)?.selectBottomTab(R.id.nav_orders)
        }
    }
}