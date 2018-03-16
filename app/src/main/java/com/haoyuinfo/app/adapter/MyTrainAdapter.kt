package com.haoyuinfo.app.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.haoyuinfo.app.R
import com.haoyuinfo.app.adapterhelper.BaseArrayRecyclerAdapter
import com.haoyuinfo.app.entity.TrainEntity

/**
 * 创建日期：2018/3/14.
 * 描述:我的培训列表适配器
 * 作者:xiaoma
 */
class MyTrainAdapter(mDatas: MutableList<TrainEntity>, private var selectItem: Int = 0) : BaseArrayRecyclerAdapter<TrainEntity>(mDatas) {

    fun setSelectedItem(position: Int) {
        selectItem = position
        notifyDataSetChanged()
    }

    override fun bindView(viewType: Int): Int {
        return R.layout.mytrain_item
    }

    override fun onBindHoder(holder: RecyclerHolder, t: TrainEntity, position: Int) {
        val mTrainTv = holder.obtainView<TextView>(R.id.mTrainTv)
        val mSelectIv = holder.obtainView<ImageView>(R.id.mSelectIv)
        mTrainTv.text = t.name
        mSelectIv.visibility = if (selectItem == position) View.VISIBLE else View.GONE
    }
}