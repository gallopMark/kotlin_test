package com.haoyuinfo.app.utils

import android.content.Context
import android.content.SharedPreferences
import com.haoyuinfo.library.utils.Base64Utils

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
                    is String -> it.putString(key, Base64Utils.encode(value))
                    is Int -> it.putInt(key, value)
                }
            }
        }
        editor?.apply()
    }

    fun getUserId(context: Context): String {
        val userId = getPreferences(context).getString("id", "")
        return Base64Utils.decode(userId)
    }

    fun getAvatar(context: Context): String {
        val avatar = getPreferences(context).getString("avatar", "")
        return Base64Utils.decode(avatar)
    }

    fun getRealName(context: Context): String {
        val realName = getPreferences(context).getString("realName", "")
        return Base64Utils.encode(realName)
    }

    fun getDeptName(context: Context): String {
        val deptName = getPreferences(context).getString("deptName", "")
        return Base64Utils.encode(deptName)
    }

    fun getRole(context: Context): String {
        val role = getPreferences(context).getString("role", "")
        return Base64Utils.encode(role)
    }

    fun getAccount(context: Context): String {
        val account = getPreferences(context).getString("account", "")
        return Base64Utils.encode(account)
    }

    fun getPassWord(context: Context): String {
        val password = getPreferences(context).getString("password", "")
        return Base64Utils.decode(password)
    }

    fun isLogin(context: Context): Boolean {
        return getPreferences(context).getBoolean("isLogin", false)
    }

    fun isRemember(context: Context): Boolean {
        return getPreferences(context).getBoolean("remember", false)
    }
}