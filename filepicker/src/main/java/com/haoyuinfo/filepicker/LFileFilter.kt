package com.haoyuinfo.filepicker

import java.io.File
import java.io.FileFilter


class LFileFilter(private val mTypes: Array<String>?) : FileFilter {

    override fun accept(file: File): Boolean {
        if (file.isDirectory) {
            return true
        }
        if (mTypes != null && mTypes.isNotEmpty()) {
            for (i in 0 until mTypes.size) {
                if (file.name.endsWith(mTypes[i].toLowerCase()) || file.name.endsWith(mTypes[i].toUpperCase())) {
                    return true
                }
            }
        } else {
            return true
        }
        return false
    }
}