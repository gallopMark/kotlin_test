package com.haoyuinfo.mediapicker.utils

import android.app.Activity
import android.content.Intent
import com.haoyuinfo.mediapicker.MediaGridActivity
import com.haoyuinfo.mediapicker.entity.MediaItem

class MediaPicker private constructor() {
    companion object {
        const val EXTRA_PATHS = "extra_paths"
    }

    private var context: Activity? = null
    private var mode = MediaItem.TYPE_PHOTO //选择类型（图片或视频）
    private var mMutilyMode = false //选择模式（单选或多选）
    private var limit = 1   //最大图片选择数量
    private var crop = false    //是否需要裁剪
    private var isSaveRectangle = true  //裁剪后的图片是否是矩形，否者跟随裁剪框的形状
    private var outPutX = 800           //裁剪保存宽度
    private var outPutY = 800           //裁剪保存高度
    private var focusWidth = 280         //焦点框的宽度
    private var focusHeight = 280        //焦点框的高度
    private var requestCode: Int = 1

    constructor(builder: Builder) : this() {
        context = builder.context
        mode = builder.mode
        mMutilyMode = builder.mMutilyMode
        limit = builder.limit
        crop = builder.crop
        isSaveRectangle = builder.isSaveRectangle
        outPutX = builder.outPutX
        outPutY = builder.outPutY
        focusWidth = builder.focusWidth
        focusHeight = builder.focusHeight
        requestCode = builder.requestCode
    }

    fun openActivity() {
        context?.let {
            val intent = Intent(it, MediaGridActivity::class.java)
            intent.putExtra("mode", mode)
            intent.putExtra("mMutilyMode", mMutilyMode)
            intent.putExtra("limit", limit)
            intent.putExtra("isCrop", crop)
            intent.putExtra("isSaveRectangle", isSaveRectangle)
            intent.putExtra("outPutX", outPutX)
            intent.putExtra("outPutY", outPutY)
            intent.putExtra("focusWidth", focusWidth)
            intent.putExtra("focusHeight", focusHeight)
            it.startActivityForResult(intent, requestCode)
        }
    }

    class Builder private constructor() {
        internal var context: Activity? = null
        internal var mode = MediaItem.TYPE_PHOTO //选择类型（图片或视频）
        internal var mMutilyMode = false //选择模式（单选或多选）
        internal var limit = 1   //最大图片选择数量
        internal var crop = false    //是否需要裁剪
        internal var isSaveRectangle = true  //裁剪后的图片是否是矩形，否者跟随裁剪框的形状
        internal var outPutX = 800           //裁剪保存宽度
        internal var outPutY = 800           //裁剪保存高度
        internal var focusWidth = 280         //焦点框的宽度
        internal var focusHeight = 280        //焦点框的高度
        internal var requestCode = 1

        constructor(context: Activity) : this() {
            this.context = context
        }

        fun mode(mode: Int): Builder {
            this.mode = mode
            return this
        }

        fun mutilyMode(isMutily: Boolean): Builder {
            this.mMutilyMode = isMutily
            return this
        }

        fun limit(limit: Int): Builder {
            this.limit = limit
            return this
        }

        fun crop(): Builder {
            this.crop = true
            return this
        }

        fun circle(): Builder {
            this.isSaveRectangle = false
            return this
        }

        fun outPutX(outPutX: Int): Builder {
            this.outPutX = outPutX
            return this
        }

        fun outPutY(outPutY: Int): Builder {
            this.outPutY = outPutY
            return this
        }

        fun focusWidth(focusWidth: Int): Builder {
            this.focusWidth = focusWidth
            return this
        }

        fun focusHeight(focusHeight: Int): Builder {
            this.focusHeight = focusHeight
            return this
        }

        fun withRequestCode(requestCode: Int): Builder {
            this.requestCode = requestCode
            return this
        }

        fun build(): MediaPicker {
            return MediaPicker(this)
        }
    }
}