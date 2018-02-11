package com.haoyuinfo.app.base

import android.app.Application

class CompatApplication : Application() {
    companion object {
        private var instance: CompatApplication? = null
        fun instance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}