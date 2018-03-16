package com.haoyuinfo.library.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationManagerCompat
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.math.BigDecimal


object Common {

    /*弹出软键盘*/
    fun showSoftInput(context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun showSoftInput(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0)
    }

    /*强制隐藏键盘*/
    fun hideSoftInput(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideSoftInput(context: Activity) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        context.currentFocus?.windowToken?.let { imm.hideSoftInputFromWindow(it, 0) }
    }

    /*是否注册了权限*/
    fun checkPermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun formatNum(num: Int): String {
        if (num < 10 * 1000) {
            return num.toString()
        } else {
            val count: Double
            val nuit: String
            if (num > 10 * 1000 && num < 10 * 1000 * 10000) {
                count = num / (10 * 1000).toDouble()
                nuit = "万"
            } else {
                count = num / (10 * 1000 * 10000).toDouble()
                nuit = "亿"
            }
            val bd = BigDecimal(count)
            return "${bd.setScale(1, BigDecimal.ROUND_HALF_UP).toDouble()}$nuit"
        }
    }

    /*通知是否已经关闭*/
    fun isNotificationEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

}