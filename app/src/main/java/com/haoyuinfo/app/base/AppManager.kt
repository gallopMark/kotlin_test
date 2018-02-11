package com.haoyuinfo.app.base

import android.app.Activity
import android.content.Context
import android.os.Process

import java.util.Stack

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 */
class AppManager private constructor() {

    companion object {
        fun get(): AppManager {
            return Instance.instance
        }
    }

    private object Instance {
        val instance = AppManager()
    }

    private val activityStack = Stack<Activity>()
    /**
     * 添加Activity到栈
     */
    fun addActivity(activity: Activity) {
        activityStack.add(activity)
    }

    fun removeActivity(activity: Activity) {
        activityStack.remove(activity)
    }

    /**
     * 应用程序退出
     */
    fun AppExit(context: Context) {
        try {
            finishAllActivity()
            Process.killProcess(Process.myPid())
            System.exit(0)
        } catch (e: Exception) {
            System.exit(0)
        }
    }

    /**
     * 结束所有Activity
     */
    private fun finishAllActivity() {
        for (activity in activityStack) {
            activity?.finish()
        }
        activityStack.clear()
    }

}
