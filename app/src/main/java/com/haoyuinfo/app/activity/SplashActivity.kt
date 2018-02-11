package com.haoyuinfo.app.activity

import android.content.Intent
import android.os.Handler
import android.view.KeyEvent
import com.haoyuinfo.app.R
import com.haoyuinfo.app.base.BaseActivity

class SplashActivity : BaseActivity() {
    override fun setLayoutResID(): Int {
        return R.layout.activity_splash
    }

    override fun setUp() {
        Handler(mainLooper).postDelayed({ enter() }, 2500)
    }

    private fun enter() {
        if (isLogin()) {
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