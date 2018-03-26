package com.haoyuinfo.app.fragment.menu

import android.content.Intent
import com.haoyuinfo.app.R
import com.haoyuinfo.app.activity.LoginActivity
import com.haoyuinfo.app.utils.PreferenceUtils
import com.haoyuinfo.library.base.AppManager
import com.haoyuinfo.library.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * 创建日期：2018/3/5.
 * 描述:设置
 * 作者:xiaoma
 */
class SettingsFragment : BaseFragment() {
    override fun setLayoutResID(): Int {
        return R.layout.fragment_settings
    }

    override fun setUp() {
        mLoginOutBt.setOnClickListener {
            PreferenceUtils.saveUser(context, HashMap<String, Any>().apply { put("isLogin", true) })
            AppManager.get().finishAllActivity()
            startActivity(Intent(context, LoginActivity::class.java))
        }
    }
}