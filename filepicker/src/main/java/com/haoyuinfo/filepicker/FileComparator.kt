package com.haoyuinfo.filepicker

import java.io.File
import java.util.*

class FileComparator : Comparator<File> {
    override fun compare(f1: File, f2: File): Int {
        if (f1 == f2) {
            return 0
        }
        if (f1.isDirectory && f2.isFile) {
            return -1
        }
        if (f1.isFile && f2.isDirectory) {
            return 1
        }
        return f1.name.compareTo(f2.name, true)
    }
}