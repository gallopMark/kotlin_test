package com.haoyuinfo.library.base

import android.app.Activity
import android.os.Process
import java.util.*

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 */
object AppManager {

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
    fun AppExit() {
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
    fun finishAllActivity() {
        for (activity in activityStack) {
            activity?.finish()
        }
        activityStack.clear()
    }

}
