package com.haoyuinfo.app.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import com.haoyuinfo.app.R
import com.haoyuinfo.library.base.BaseActivity
import com.uuzuche.zxing.ui.CaptureFragment
import com.uuzuche.zxing.utils.CodeUtils

class CaptureActivity : BaseActivity() {

    override fun setLayoutResID(): Int {
        return R.layout.activity_capture
    }

    override fun setUp(savedInstanceState: Bundle?) {
        setToolTitle(resources.getString(R.string.qr_code))
        val fragment = CaptureFragment()
        // 为二维码扫描界面设置定制化界面
        fragment.setAnalyzeCallBack(analyzeCallback)
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    /**
     * 二维码解析回调函数
     */
    private var analyzeCallback: CodeUtils.AnalyzeCallback = object : CodeUtils.AnalyzeCallback {
        override fun onAnalyzeSuccess(mBitmap: Bitmap, result: String) {
            val intent = Intent()
            intent.putExtra(CodeUtils.RESULT_STRING, result)
            setResult(RESULT_OK, intent)
            finish()
        }

        override fun onAnalyzeFailed() {

        }
    }
}