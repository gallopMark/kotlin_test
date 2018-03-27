package com.haoyuinfo.app.fragment.page

import android.view.View
import com.haoyuinfo.app.R
import com.haoyuinfo.library.base.BasePageFragment

/**
 * 创建日期：2018/3/15.
 * 描述:课程问答
 * 作者:xiaoma
 */
class QuestionFragment : BasePageFragment() {
    private var courseId: String? = null
    override fun setLayoutResID(): Int {
        return R.layout.fragment_course_question
    }

    override fun setUp(view: View) {
        courseId = arguments?.getString("courseId")
    }

}