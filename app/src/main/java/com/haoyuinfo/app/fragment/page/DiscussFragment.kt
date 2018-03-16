package com.haoyuinfo.app.fragment.page

import android.util.Log
import com.haoyuinfo.app.R
import com.haoyuinfo.app.utils.Constants
import com.haoyuinfo.library.base.BaseLazyFragment

/**
 * 创建日期：2018/3/15.
 * 描述:课程讨论
 * 作者:xiaoma
 */
class DiscussFragment : BaseLazyFragment() {
    private var courseId: String? = null
    private var page = 1
    override fun setLayoutResID(): Int {
        return R.layout.fragment_course_discuss
    }

    override fun setUp() {
        courseId = arguments?.getString("courseId")
    }

    override fun initData() {
        val url = String.format(Constants.COURSE_DISCUSS, courseId, page)
        Log.e("url", url)
    }
}