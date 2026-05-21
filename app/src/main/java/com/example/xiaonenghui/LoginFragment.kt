package com.example.xiaonenghui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButtonToggleGroup

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val signInStudentId = view.findViewById<TextInputEditText>(R.id.sign_in_student_id)
        val signInPassword = view.findViewById<TextInputEditText>(R.id.sign_in_password)

        val signUpStudentId = view.findViewById<TextInputEditText>(R.id.sign_up_student_id)
        val signUpPassword = view.findViewById<TextInputEditText>(R.id.sign_up_password)
        val signUpConfirmPassword = view.findViewById<TextInputEditText>(R.id.sign_up_confirm_password)

        val toggleGroup = view.findViewById<MaterialButtonToggleGroup>(R.id.auth_toggle_group)
        val signInContainer = view.findViewById<View>(R.id.sign_in_container)
        val signUpContainer = view.findViewById<View>(R.id.sign_up_container)

        toggleGroup.check(R.id.toggle_sign_in)
        signInContainer.visibility = View.VISIBLE
        signUpContainer.visibility = View.GONE

        toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val isSignIn = checkedId == R.id.toggle_sign_in
            signInContainer.visibility = if (isSignIn) View.VISIBLE else View.GONE
            signUpContainer.visibility = if (isSignIn) View.GONE else View.VISIBLE
        }

        view.findViewById<Button>(R.id.sign_in_button).setOnClickListener {
            val studentId = signInStudentId.text.toString().trim()
            val password = signInPassword.text.toString().trim()

            signInStudentId.error = null
            signInPassword.error = null

            if (studentId.isEmpty()) {
                signInStudentId.error = "学号不能为空"
                signInStudentId.requestFocus()
                return@setOnClickListener
            }

            if (studentId.length != 10) {
                signInStudentId.error = "学号错误"
                signInStudentId.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                signInPassword.error = "密码不能为空"
                signInPassword.requestFocus()
                return@setOnClickListener
            }

            showSuccessThenGoHome(view)
        }

        view.findViewById<Button>(R.id.sign_up_button).setOnClickListener {
            val studentId = signUpStudentId.text.toString().trim()
            val password = signUpPassword.text.toString().trim()
            val confirmPassword = signUpConfirmPassword.text.toString().trim()

            signUpStudentId.error = null
            signUpPassword.error = null
            signUpConfirmPassword.error = null

            if (studentId.isEmpty()) {
                signUpStudentId.error = "学号不能为空"
                signUpStudentId.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                signUpPassword.error = "密码不能为空"
                signUpPassword.requestFocus()
                return@setOnClickListener
            }

            if (studentId.length != 10){
                signUpStudentId.error = "学号错误"
                signUpStudentId.requestFocus()
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {
                signUpConfirmPassword.error = "再次输入密码不能为空"
                signUpConfirmPassword.requestFocus()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                signUpConfirmPassword.error = "两次密码必须一样"
                signUpConfirmPassword.requestFocus()
                return@setOnClickListener
            }

            showSuccessThenGoHome(view)
        }
    }

    private fun showSuccessThenGoHome(view: View) {
        val successBox = view.findViewById<View>(R.id.auth_success_box)
        val signInButton = view.findViewById<Button>(R.id.sign_in_button)
        val signUpButton = view.findViewById<Button>(R.id.sign_up_button)

        signInButton.isEnabled = false
        signUpButton.isEnabled = false

        successBox.visibility = View.VISIBLE
        successBox.alpha = 0f
        successBox.translationY = 24f

        successBox.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .start()

        view.postDelayed({
            if (!isAdded) return@postDelayed
            (activity as? MainActivity)?.setLoggedIn(true)
            (activity as? MainActivity)?.showMainUi()
        }, 2000)
    }
}