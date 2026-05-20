package com.example.xiaonenghui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> switchFragment(HomeFragment())
                R.id.nav_services -> switchFragment(ServicesFragment())
                R.id.nav_post -> switchFragment(PostFragment())
                R.id.nav_orders -> switchFragment(OrdersFragment())
                R.id.nav_profile -> switchFragment(ProfileFragment())
            }
            true
        }

        if (savedInstanceState == null) {
            if (isLoggedIn()) {
                showMainUi()
            } else {
                showLoginUi()
            }
        }
    }

    fun showMainUi() {
        findViewById<BottomNavigationView>(R.id.bottom_nav).visibility = View.VISIBLE
        switchFragment(HomeFragment())
        findViewById<BottomNavigationView>(R.id.bottom_nav).selectedItemId = R.id.nav_home
    }

    private fun showLoginUi() {
        findViewById<BottomNavigationView>(R.id.bottom_nav).visibility = View.GONE
        switchFragment(LoginFragment())
    }

    private fun isLoggedIn(): Boolean {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return prefs.getBoolean("is_logged_in", false)
    }

    fun setLoggedIn(value: Boolean) {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        prefs.edit().putBoolean("is_logged_in", value).apply()
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}