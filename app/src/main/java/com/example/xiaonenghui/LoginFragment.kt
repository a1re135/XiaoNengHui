package com.example.xiaonenghui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
            (activity as? MainActivity)?.setLoggedIn(true)
            (activity as? MainActivity)?.showMainUi()
        }

        view.findViewById<Button>(R.id.sign_up_button).setOnClickListener {
            (activity as? MainActivity)?.setLoggedIn(true)
            (activity as? MainActivity)?.showMainUi()
        }

        view.findViewById<TextView>(R.id.forgot_password).setOnClickListener {
            // Placeholder for future flow
        }
    }
}

