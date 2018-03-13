@file:Suppress("DEPRECATION")

package com.uuzuche.zxing.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Camera
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.common.HybridBinarizer
import com.uuzuche.zxing.camera.BitmapLuminanceSource
import com.uuzuche.zxing.camera.CameraManager
import com.uuzuche.zxing.decoding.DecodeFormatManager
import java.util.*

object CodeUtils {
    const val RESULT_TYPE = "result_type"
    const val RESULT_STRING = "result_string"
    const val RESULT_SUCCESS = 1
    const val RESULT_FAILED = 2

    interface AnalyzeCallback {
        fun onAnalyzeSuccess(mBitmap: Bitmap, result: String)

        fun onAnalyzeFailed()
    }

    /*解析二维码图片工具类*/
    fun analyzeBitmap(path: String, analyzeCallback: AnalyzeCallback?) {
        /* 首先判断图片的大小,若图片过大,则执行图片的裁剪操作,防止OOM*/
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true // 先获取原大小
        var mBitmap = BitmapFactory.decodeFile(path, options)
        options.inJustDecodeBounds = false // 获取新的大小
        var sampleSize = (options.outHeight / 400.toFloat()).toInt()
        if (sampleSize <= 0)
            sampleSize = 1
        options.inSampleSize = sampleSize
        mBitmap = BitmapFactory.decodeFile(path, options)
        val multiFormatReader = MultiFormatReader()
        // 解码的参数
        val hints = Hashtable<DecodeHintType, Any>(2)
        // 可以解析的编码类型
        val decodeFormats = Vector<BarcodeFormat>()
        if (decodeFormats.isEmpty()) {
            // 这里设置可扫描的类型，我这里选择了都支持
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS)
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS)
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS)
        }
        hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
        // 设置继续的字符编码格式为UTF8
        // 设置解析配置参数
        multiFormatReader.setHints(hints)
        // 开始对图像资源解码
        try {
            val rawResult = multiFormatReader.decodeWithState(BinaryBitmap(HybridBinarizer(BitmapLuminanceSource(mBitmap))))
            analyzeCallback?.onAnalyzeSuccess(mBitmap, rawResult.text)
        } catch (e: Exception) {
            e.printStackTrace()
            analyzeCallback?.onAnalyzeFailed()
        }
    }

    fun isLightEnable(isEnable: Boolean) {
        if (isEnable) {
            val camera = CameraManager.get().camera
            camera?.let {
                val parameter = it.parameters
                parameter.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                it.parameters = parameter
            }
        } else {
            val camera = CameraManager.get().camera
            camera?.let {
                val parameter = it.parameters
                parameter.flashMode = Camera.Parameters.FLASH_MODE_OFF
                it.parameters = parameter
            }
        }
    }
}