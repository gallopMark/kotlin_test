package com.haoyuinfo.app.fragment.page

import android.util.Log
import com.haoyuinfo.app.R
import com.haoyuinfo.app.utils.Constants
import com.haoyuinfo.library.base.BaseLazyFragment

/**
 * 创建日期：2018/3/15.
 * 描述:课程学习进度
 * 作者:xiaoma
 */
class ProgressFragment : BaseLazyFragment() {
    private var courseId: String? = null
    override fun setLayoutResID(): Int {
        return R.layout.fragment_course_progress
    }

    override fun setUp() {
        courseId = arguments?.getString("courseId")
    }

    override fun initData() {
        val url = String.format(Constants.COURSE_PROGRESS, courseId, courseId)
        Log.e("url", url)
    }
}