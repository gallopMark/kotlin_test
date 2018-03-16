package com.haoyuinfo.app.fragment.page

import com.haoyuinfo.app.R
import com.haoyuinfo.app.entity.CourseLearnResult
import com.haoyuinfo.app.utils.Constants
import com.haoyuinfo.app.utils.OkHttpUtils
import com.haoyuinfo.library.base.BaseLazyFragment
import com.haoyuinfo.library.widget.CurrencyLoadView
import kotlinx.android.synthetic.main.fragment_course_learn.*
import okhttp3.Request

/**
 * 创建日期：2018/3/15.
 * 描述:课程学习
 * 作者:xiaoma
 */
class LearnFragment : BaseLazyFragment() {
    private var training = false
    private var courseId: String? = null

    override fun setLayoutResID(): Int {
        return R.layout.fragment_course_learn
    }

    override fun setUp() {
        arguments?.let {
            training = it.getBoolean("training")
            courseId = it.getString("courseId")
        }
    }

    override fun initData() {
        val url = String.format(Constants.COURSE_LEARN, courseId, courseId)
        OkHttpUtils.getAsync(context, url, object : OkHttpUtils.ResultCallback<CourseLearnResult>() {
            override fun onBefore(request: Request) {
                loadView.setState(CurrencyLoadView.STATE_LOADING)
            }

            override fun onError(request: Request, e: Throwable) {
                loadView.setState(CurrencyLoadView.STATE_ERROR)
            }

            override fun onResponse(response: CourseLearnResult?) {
                loadView.setState(CurrencyLoadView.STATE_GONE)
            }
        })
    }
}