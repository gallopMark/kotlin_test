package com.haoyuinfo.app.fragment.menu

import com.haoyuinfo.app.R
import com.haoyuinfo.library.base.BaseFragment
import tv.danmaku.ijk.media.ui.VideoPlayerActivity

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
        VideoPlayerActivity.openVideo(context, "", true)
    }
}