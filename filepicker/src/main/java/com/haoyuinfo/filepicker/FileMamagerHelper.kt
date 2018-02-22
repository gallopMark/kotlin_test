package com.haoyuinfo.filepicker

import android.text.TextUtils
import java.io.File
import java.io.FileFilter
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

object FileMamagerHelper {

    fun getFilesByPath(path: String?, filter: FileFilter): ArrayList<File> {
        if (TextUtils.isEmpty(path)) return ArrayList()
        return getFilesByFile(File(path), filter)
    }

    fun getFilesByFile(file: File?, filter: FileFilter): ArrayList<File> {
        if (file != null && file.exists()) {
            val files = file.listFiles(filter) ?: return ArrayList()
            val list = ArrayList<File>()
            for (f in files) {
                list.add(f)
            }
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

    fun getReadableFileSize(fileSize: Long): String {
        if (fileSize <= 0) return "0B"
        val units = arrayOf("B", "KB", "M", "G", "T")
        val digitGroups = (Math.log10(fileSize.toDouble()) / Math.log10(1024.toDouble())).toInt()
        return DecimalFormat("#.0").format(fileSize / Math.pow(1024.toDouble(), fileSize.toDouble())) + units[digitGroups]
    }
}