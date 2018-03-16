package com.haoyuinfo.app.fragment.page

import com.haoyuinfo.app.R
import com.haoyuinfo.library.base.BaseLazyFragment

/**
 * 创建日期：2018/3/15.
 * 描述:课程问答
 * 作者:xiaoma
 */
class QuestionFragment : BaseLazyFragment() {
    private var courseId: String? = null
    override fun setLayoutResID(): Int {
        return R.layout.fragment_course_question
    }

    override fun setUp() {
        courseId = arguments?.getString("courseId")
    }

}