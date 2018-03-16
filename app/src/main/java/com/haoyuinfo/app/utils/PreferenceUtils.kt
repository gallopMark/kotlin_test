package com.haoyuinfo.app.utils

import android.content.Context
import android.content.SharedPreferences

object PreferenceUtils {
    private const val name = "Prefs_user"
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    fun saveUser(context: Context, map: Map<String, Any>) {
        val editor = getPreferences(context).edit()
        editor?.let {
            for ((key, value) in map) {
                when (value) {
                    is Boolean -> it.putBoolean(key, value)
                    is String -> it.putString(key, value)
                    is Int -> it.putInt(key, value)
                }
            }
        }
        editor?.apply()
    }

    fun getUserId(context: Context): String {
        return getPreferences(context).getString("id", "")
    }

    fun getAvatar(context: Context): String {
        return getPreferences(context).getString("avatar", "")
    }

    fun getRealName(context: Context): String {
        return getPreferences(context).getString("realName", "")
    }

    fun getDeptName(context: Context): String {
        return getPreferences(context).getString("deptName", "")
    }

    fun getRole(context: Context): String {
        return getPreferences(context).getString("role", "")
    }

    fun getAccount(context: Context): String {
        return getPreferences(context).getString("account", "")
    }

    fun getPassWord(context: Context): String {
        return getPreferences(context).getString("password", "")
    }

    fun isLogin(context: Context): Boolean {
        return getPreferences(context).getBoolean("isLogin", false)
    }

    fun isRemember(context: Context): Boolean {
        return getPreferences(context).getBoolean("remember", false)
    }
}