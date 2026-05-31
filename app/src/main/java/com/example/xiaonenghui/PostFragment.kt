package com.example.xiaonenghui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PostFragment : Fragment() {

    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
    private var selectedTime: Calendar? = null

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
        val inputPrice = view.findViewById<TextInputEditText>(R.id.input_task_price)
        val inputLocation = view.findViewById<TextInputEditText>(R.id.input_task_location)
        val inputTime = view.findViewById<TextInputEditText>(R.id.input_task_time)
        val inputSchedule = view.findViewById<TextInputEditText>(R.id.input_task_schedule)
        val inputDescription = view.findViewById<TextInputEditText>(R.id.input_task_description)
        val publishButton = view.findViewById<MaterialButton>(R.id.button_publish_task)
        val titleLayout = view.findViewById<TextInputLayout>(R.id.layout_task_title)
        val priceLayout = view.findViewById<TextInputLayout>(R.id.layout_task_price)
        val locationLayout = view.findViewById<TextInputLayout>(R.id.layout_task_location)
        val descriptionLayout = view.findViewById<TextInputLayout>(R.id.layout_task_description)
        val timeLayout = view.findViewById<TextInputLayout>(R.id.layout_task_time)
        val notificationButton = view.findViewById<View>(R.id.button_post_notifications)
        val schoolButton = view.findViewById<View>(R.id.button_post_school)

        val typeTutor = view.findViewById<TextView>(R.id.chip_type_tutor)
        val typeCreative = view.findViewById<TextView>(R.id.chip_type_creative)
        val typeErrand = view.findViewById<TextView>(R.id.chip_type_errand)
        val typePrint = view.findViewById<TextView>(R.id.chip_type_print)
        val typeOther = view.findViewById<TextView>(R.id.chip_type_other)
        val typeViews = listOf(typeTutor, typeCreative, typeErrand, typePrint, typeOther)
        var selectedTypeView: TextView = typeCreative

        updateTypeSelection(typeViews, selectedTypeView)
        typeViews.forEach { textView ->
            textView.setOnClickListener {
                selectedTypeView = textView
                updateTypeSelection(typeViews, selectedTypeView)
            }
        }

        val openDateTimePicker = {
            showDateTimePicker(inputTime)
        }
        inputTime.setOnClickListener { openDateTimePicker() }
        timeLayout.setEndIconOnClickListener { openDateTimePicker() }

        val updatePublishState = {
            val title = inputTitle.text.toString().trim()
            val price = inputPrice.text.toString().trim()
            val location = inputLocation.text.toString().trim()
            val time = inputTime.text.toString().trim()
            val description = inputDescription.text.toString().trim()
            val enabled =
                title.isNotEmpty() && price.isNotEmpty() && location.isNotEmpty() && time.isNotEmpty() && description.isNotEmpty()
            publishButton.isEnabled = enabled
            publishButton.alpha = if (enabled) 1f else 0.6f
        }

        inputTitle.doAfterTextChanged { updatePublishState() }
        inputPrice.doAfterTextChanged { updatePublishState() }
        inputLocation.doAfterTextChanged { updatePublishState() }
        inputTime.doAfterTextChanged { updatePublishState() }
        inputDescription.doAfterTextChanged { updatePublishState() }
        updatePublishState()

        notificationButton.setOnClickListener {
            showNotifications()
        }

        schoolButton.setOnClickListener {
            (activity as? MainActivity)?.selectBottomTab(R.id.nav_home)
        }

        publishButton.setOnClickListener {
            val title = inputTitle.text.toString().trim()
            val price = inputPrice.text.toString().trim()
            val location = inputLocation.text.toString().trim()
            val time = inputTime.text.toString().trim()
            val schedule = inputSchedule.text.toString().trim()
            val description = inputDescription.text.toString().trim()

            titleLayout.error = null
            priceLayout.error = null
            locationLayout.error = null
            timeLayout.error = null
            descriptionLayout.error = null

            if (title.isEmpty()) {
                titleLayout.error = "任务标题不能为空"
                inputTitle.requestFocus()
                return@setOnClickListener
            }

            if (price.isEmpty()) {
                priceLayout.error = "预算金额不能为空"
                inputPrice.requestFocus()
                return@setOnClickListener
            }

            if (location.isEmpty()) {
                locationLayout.error = "任务地点不能为空"
                inputLocation.requestFocus()
                return@setOnClickListener
            }

            if (time.isEmpty()) {
                timeLayout.error = "期望完成时间不能为空"
                inputTime.requestFocus()
                return@setOnClickListener
            }

            if (description.isEmpty()) {
                descriptionLayout.error = "任务描述不能为空"
                inputDescription.requestFocus()
                return@setOnClickListener
            }

            val category = selectedTypeView.text?.toString()?.trim().orEmpty()
            val mergedDescription = description
            val finalSchedule = schedule.ifEmpty { time }

            val newService = ServiceItem(
                title = title,
                category = if (category.isEmpty()) "其他" else category,
                provider = "发布者",
                price = "${price}元",
                rating = "新",
                description = mergedDescription,
                location = location,
                schedule = finalSchedule
            )

            AppDataStore.latestPostedService = newService
            AppDataStore.services.add(0, newService)

            Toast.makeText(requireContext(), "任务发布成功", Toast.LENGTH_SHORT).show()

            inputTitle.text?.clear()
            inputPrice.text?.clear()
            inputLocation.text?.clear()
            inputTime.text?.clear()
            inputSchedule.text?.clear()
            inputDescription.text?.clear()

            selectedTime = null
            updatePublishState()

            (activity as? MainActivity)?.selectBottomTab(R.id.nav_services)
        }
    }

    private fun showDateTimePicker(targetView: TextInputEditText) {
        val now = selectedTime ?: Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedDate = (selectedTime ?: Calendar.getInstance()).apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                }
                TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->
                        selectedDate.set(Calendar.HOUR_OF_DAY, hour)
                        selectedDate.set(Calendar.MINUTE, minute)
                        selectedTime = selectedDate
                        targetView.setText(dateTimeFormat.format(selectedDate.time))
                    },
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    true
                ).show()
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateTypeSelection(typeViews: List<TextView>, selectedView: TextView) {
        val selectedBackground = R.drawable.bg_circle_primary
        val selectedText = ContextCompat.getColor(requireContext(), R.color.blue_primary)
        val unselectedBackground = R.drawable.bg_chip_neutral
        val unselectedText = ContextCompat.getColor(requireContext(), R.color.blue_on_surface)

        typeViews.forEach { textView ->
            val isSelected = textView.id == selectedView.id
            textView.setBackgroundResource(if (isSelected) selectedBackground else unselectedBackground)
            textView.setTextColor(if (isSelected) selectedText else unselectedText)
        }
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
}