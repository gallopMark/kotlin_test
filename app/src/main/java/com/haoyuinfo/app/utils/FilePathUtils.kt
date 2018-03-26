package com.haoyuinfo.app.utils

import android.content.Context

object FilePathUtils {
    fun getDownloadFilePath(context: Context): String {
        return context.getExternalFilesDir("").absolutePath
    }
}