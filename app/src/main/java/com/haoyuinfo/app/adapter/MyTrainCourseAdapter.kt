package com.haoyuinfo.app.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.haoyuinfo.app.R
import com.haoyuinfo.app.adapterhelper.BaseArrayRecyclerAdapter
import com.haoyuinfo.app.entity.CourseEntity
import com.haoyuinfo.library.utils.GlideUtils
import com.haoyuinfo.library.utils.ScreenUtils

/**
 * 创建日期：2018/3/14.
 * 描述:培训下的课程列表适配器
 * 作者:xiaoma
 */
class MyTrainCourseAdapter(private val context: Context, mDatas: MutableList<CourseEntity>) : BaseArrayRecyclerAdapter<CourseEntity>(mDatas) {

    private val width = ScreenUtils.getScreenWidth(context) / 3 - 20
    private val height = width / 3 * 2

    override fun bindView(viewType: Int): Int {
        return R.layout.mytrain_course_item
    }

    override fun onBindHoder(holder: RecyclerHolder, t: CourseEntity, position: Int) {
        val ivImage = holder.obtainView<ImageView>(R.id.ivImage)
        val tvTitle = holder.obtainView<TextView>(R.id.tvTitle)
        val tvType = holder.obtainView<TextView>(R.id.tvType)
        val tvHours = holder.obtainView<TextView>(R.id.tvHours)
        val divider = holder.obtainView<View>(R.id.divider)
        ivImage.layoutParams = LinearLayout.LayoutParams(width, height)
        GlideUtils.loadImage(context, t.image, R.drawable.ic_default, R.drawable.ic_default, ivImage)
        tvTitle.text = t.title
        tvType.text = t.type
        val studyHours = "${t.studyHours}学时"
        tvHours.text = studyHours
        divider.visibility = if (position != itemCount - 1) View.VISIBLE else View.GONE
    }

}