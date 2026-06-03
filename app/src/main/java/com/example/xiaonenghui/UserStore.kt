package com.example.xiaonenghui

import android.content.Context
import android.content.SharedPreferences

object UserStore {
    private const val PREF_NAME = "user_store"
    private const val KEY_CURRENT_USER_ID = "current_user_id"
    private const val USER_PREFIX = "user_"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun registerUser(context: Context, studentId: String, name: String, password: String): Boolean {
        val prefs = getPrefs(context)
        if (prefs.contains("$USER_PREFIX$studentId")) return false

        prefs.edit().putString("$USER_PREFIX$studentId", "$name|$password|需求方").apply()

        setCurrentUser(context, studentId)
        AppDataStore.currentRole = "需求方"
        return true
    }

    fun loginUser(context: Context, studentId: String, password: String): Boolean {
        val prefs = getPrefs(context)
        val data = prefs.getString("$USER_PREFIX$studentId", null) ?: return false
        val parts = data.split("|")

        if (parts.size >= 2 && parts[1] == password) {
            setCurrentUser(context, studentId)
            if (parts.size >= 3) {
                AppDataStore.currentRole = parts[2]
            }
            return true
        }
        return false
    }

    private fun setCurrentUser(context: Context, id: String) {
        getPrefs(context).edit().putString(KEY_CURRENT_USER_ID, id).apply()
    }

    fun getCurrentStudentId(context: Context): String? = 
        getPrefs(context).getString(KEY_CURRENT_USER_ID, null)

    fun getCurrentUserName(context: Context): String {
        val id = getCurrentStudentId(context) ?: return "未登录"
        val data = getPrefs(context).getString("$USER_PREFIX$id", null) ?: return "未知用户"
        return data.split("|").getOrNull(0) ?: "未知用户"
    }

    fun logout(context: Context) {
        getPrefs(context).edit().remove(KEY_CURRENT_USER_ID).apply()
    }

    fun updateUserRole(context: Context, role: String) {
        val id = getCurrentStudentId(context) ?: return
        val prefs = getPrefs(context)
        val data = prefs.getString("$USER_PREFIX$id", null) ?: return
        val parts = data.split("|").toMutableList()
        if (parts.size >= 3) {
            parts[2] = role
        } else {
            parts.add(role)
        }
        prefs.edit().putString("$USER_PREFIX$id", parts.joinToString("|")).apply()
    }
}
