package com.haoyuinfo.library.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory

object BitmapUtils {
    /**
     * 根据图片实际尺寸和待显示尺寸计算图片压缩比率
     * @param options
     * @param reqWidth      显示宽度
     * @param reqHeight     显示高度
     * @return              压缩比率
     */
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        //实际宽、高
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }

    fun decodeSampledBitmapFromResource(res: Resources, resId: Int, reqWidth: Int, reqHeight: Int): Bitmap {
        val options = BitmapFactory.Options()
        //不压缩，获取实际宽、高
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, options)
        // 计算压缩比率
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(res, resId, options)
    }
}