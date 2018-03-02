package com.haoyuinfo.filepicker

import android.text.TextUtils
import java.io.File
import java.io.FileFilter
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


object FileHelper {

    fun getFilesByPath(path: String?, filter: FileFilter): MutableList<File> {
        if (TextUtils.isEmpty(path)) return ArrayList()
        return getFilesByFile(File(path), filter)
    }

    private fun getFilesByFile(file: File, filter: FileFilter): MutableList<File> {
        if (file.exists()) {
            val files = file.listFiles(filter) ?: return ArrayList()
            val list = files.toMutableList()
            Collections.sort(list, FileComparator())
            return list
        }
        return ArrayList()
    }

    fun hasParent(path: String?): Boolean {
        if (TextUtils.isEmpty(path)) return false
        return hasParent(File(path))
    }

    fun hasParent(file: File?): Boolean {
        if (file != null && file.exists()) {
            return file.parentFile != null
        }
        return false
    }

    fun getParent(path: String?): String? {
        if (TextUtils.isEmpty(path)) return null
        return File(path).parent
    }

    fun getReadableFileSize(size: Long): String {
        if (size <= 0) return "0"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
    }
}