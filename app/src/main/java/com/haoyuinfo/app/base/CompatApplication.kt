package com.haoyuinfo.app.base

import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.tencent.bugly.crashreport.CrashReport

class CompatApplication : MultiDexApplication() {
    companion object {
        private var instance: CompatApplication? = null
        fun instance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        MultiDex.install(this)
        CrashReport.initCrashReport(this)
    }

}