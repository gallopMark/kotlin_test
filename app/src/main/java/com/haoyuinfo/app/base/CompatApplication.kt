package com.haoyuinfo.app.base

import android.app.Application
import com.tencent.bugly.crashreport.CrashReport

class CompatApplication : Application() {
    companion object {
        private var instance: CompatApplication? = null
        fun instance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
//        MultiDex.install(this)
        CrashReport.initCrashReport(this)
    }

}