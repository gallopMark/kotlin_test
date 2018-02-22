package com.haoyuinfo.filepicker

import android.content.Context
import android.content.Intent

class FilePicker {
    private var context: Context? = null
    private var mRequestCode: Int = 0
    private var mMutilyMode = false
    private var mFileTypes: Array<String>? = null
    fun with(context: Context): FilePicker {
        this.context = context
        return this
    }

    fun isMutilyMode(isMutily: Boolean): FilePicker {
        this.mMutilyMode = isMutily
        return this
    }

    fun withFileFilter(fileTypes: Array<String>): FilePicker {
        this.mFileTypes = fileTypes
        return this
    }

    fun withRequestCode(requestCode: Int): FilePicker {
        this.mRequestCode = requestCode
        return this
    }

    fun start() {
        context?.let {
            val intent = Intent(it, FilePickerActivity::class.java)
            intent.putExtra("fileTypes", mFileTypes)
            intent.putExtra("isMutily", mMutilyMode)
            it.startActivity(intent)
        }
    }

}