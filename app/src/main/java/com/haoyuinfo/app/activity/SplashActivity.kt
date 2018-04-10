package com.haoyuinfo.app.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import com.haoyuinfo.app.R
import com.haoyuinfo.app.utils.PreferenceUtils
import com.haoyuinfo.library.base.BaseActivity
import com.haoyuinfo.library.utils.BitmapUtils
import com.haoyuinfo.library.utils.ScreenUtils
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {
    override fun setLayoutResID(): Int {
        return R.layout.activity_splash
    }

    override fun setUp(savedInstanceState: Bundle?) {
        val width = ScreenUtils.getScreenWidth(this)
        val height = ScreenUtils.getScreenHeight(this)
        val bitmap = BitmapUtils.decodeSampledBitmapFromResource(resources, R.drawable.splash_bg, width, height)
        mSplashIv.setImageBitmap(bitmap)
        Handler(mainLooper).postDelayed({ enter() }, 2500)
    }

    private fun enter() {
        if (PreferenceUtils.isLogin(this)) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            true
        } else super.onKeyDown(keyCode, event)
    }
}