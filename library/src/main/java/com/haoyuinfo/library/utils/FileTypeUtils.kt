package com.haoyuinfo.library.utils

import android.content.Context
import com.haoyuinfo.library.R
import java.io.File


object FileTypeUtils {

    fun isDirector(path: String?): Boolean {
        if (path == null) return false
        return File(path).isDirectory
    }

    fun isCertificate(path: String?): Boolean {
        val fileTypes = arrayOf("cer", "der", "pfx", "p12", "arm", "pem")
        return contains(path, fileTypes)
    }

    fun isExcel(path: String?): Boolean {
        val fileTypes = arrayOf("xls", "xlk", "xlsb", "xlsm", "xlsx", "xlr", "xltm", "xlw", "numbers", "ods", "ots")
        return contains(path, fileTypes)
    }

    fun isImage(path: String?): Boolean {
        val fileTypes = arrayOf("bmp", "gif", "ico", "jpeg", "jpg", "pcx", "png", "psd", "tga", "tiff", "tif", "xcf")
        return contains(path, fileTypes)
    }

    fun isMusic(path: String?): Boolean {
        val fileTypes = arrayOf("aiff", "aif", "wav", "flac", "m4a", "wma", "amr", "mp2", "mp3", "wma", "aac", "mid", "m3u")
        return contains(path, fileTypes)
    }

    fun isVideo(path: String?): Boolean {
        val fileTypes = arrayOf("avi", "mov", "wmv", "mkv", "3gp", "f4v", "flv", "mp4", "mpeg", "webm")
        return contains(path, fileTypes)
    }

    fun isPdf(path: String?): Boolean {
        val fileTypes = arrayOf("pdf")
        return contains(path, fileTypes)
    }

    fun isPowerPoint(path: String?): Boolean {
        val fileTypes = arrayOf("pptx", "keynote", "ppt", "pps", "pot", "odp", "otp")
        return contains(path, fileTypes)
    }

    fun isWord(path: String?): Boolean {
        val fileTypes = arrayOf("doc", "docm", "docx", "dot", "mcw", "rtf", "pages", "odt", "ott")
        return contains(path, fileTypes)
    }

    fun isArchive(path: String?): Boolean {
        val fileTypes = arrayOf("cab", "7z", "alz", "arj", "bzip2", "bz2", "dmg", "gzip", "gz", "jar", "lz", "lzip", "lzma", "zip", "rar", "tar", "tgz")
        return contains(path, fileTypes)
    }

    fun isApk(path: String?): Boolean {
        val fileTypes = arrayOf("apk")
        return contains(path, fileTypes)
    }

    private fun contains(path: String?, fileTypes: Array<String>): Boolean {
        if (path == null)
            return false
        val lastDot = path.lastIndexOf(".")
        if (lastDot < 0)
            return false
        val fileType = path.substring(lastDot + 1).toLowerCase()
        return fileTypes.contains(fileType)
    }

    fun getDescription(context: Context, path: String?): String {
        if (isDirector(path)) return context.resources.getString(R.string.type_directory)
        return when {
            isCertificate(path) -> context.resources.getString(R.string.type_certificate)
            isExcel(path) -> context.resources.getString(R.string.type_excel)
            isImage(path) -> context.resources.getString(R.string.type_image)
            isMusic(path) -> context.resources.getString(R.string.type_music)
            isVideo(path) -> context.resources.getString(R.string.type_video)
            isPdf(path) -> context.resources.getString(R.string.type_pdf)
            isPowerPoint(path) -> context.resources.getString(R.string.type_power_point)
            isWord(path) -> context.resources.getString(R.string.type_word)
            isArchive(path) -> context.resources.getString(R.string.type_archive)
            isApk(path) -> context.resources.getString(R.string.type_apk)
            else -> context.resources.getString(R.string.type_document)
        }
    }

    fun getIco(path: String?): Int {
        if (isDirector(path)) return R.drawable.ic_folder_48dp
        return when {
            isCertificate(path) -> R.drawable.ic_certificate_box
            isExcel(path) -> R.drawable.ic_excel_box
            isImage(path) -> R.drawable.ic_image_box
            isMusic(path) -> R.drawable.ic_music_box
            isVideo(path) -> R.drawable.ic_video_box
            isPdf(path) -> R.drawable.ic_pdf_box
            isPowerPoint(path) -> R.drawable.ic_powerpoint_box
            isWord(path) -> R.drawable.ic_word_box
            isArchive(path) -> R.drawable.ic_zip_box
            isApk(path) -> R.drawable.ic_apk_box
            else -> R.drawable.ic_document_box
        }
    }
}