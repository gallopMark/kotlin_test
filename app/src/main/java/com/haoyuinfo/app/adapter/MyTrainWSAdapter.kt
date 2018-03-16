package com.haoyuinfo.app.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.haoyuinfo.app.R
import com.haoyuinfo.app.adapterhelper.BaseArrayRecyclerAdapter
import com.haoyuinfo.app.entity.WorkShop
import com.haoyuinfo.library.utils.GlideUtils
import com.haoyuinfo.library.utils.ScreenUtils

/**
 * 创建日期：2018/3/15.
 * 描述:培训下工作坊列表适配器
 * 作者:xiaoma
 */
class MyTrainWSAdapter(private val context: Context, mDatas: MutableList<WorkShop>) : BaseArrayRecyclerAdapter<WorkShop>(mDatas) {
    private val width = ScreenUtils.getScreenWidth(context) / 3 - 20
    private val height = width / 3 * 2
    override fun bindView(viewType: Int): Int {
        return R.layout.mytrainws_item
    }

    override fun onBindHoder(holder: RecyclerHolder, t: WorkShop, position: Int) {
        val ivImage = holder.obtainView<ImageView>(R.id.ivImage)
        val tvTitle = holder.obtainView<TextView>(R.id.tvTitle)
        val tvHours = holder.obtainView<TextView>(R.id.tvHours)
        val tvScore = holder.obtainView<TextView>(R.id.tvScore)
        val divider = holder.obtainView<View>(R.id.divider)
        ivImage.layoutParams = LinearLayout.LayoutParams(width, height)
        GlideUtils.loadImage(context, t.imageUrl, R.drawable.ic_default, R.drawable.ic_default, ivImage)
        tvTitle.text = t.title
        val hours = "${t.studyHours}学时"
        tvHours.text = hours
        val score = "获得${t.point.toInt()}/${t.qualifiedPoint}积分"
        tvScore.text = score
        divider.visibility = if (position != itemCount - 1) View.VISIBLE else View.GONE
    }
}