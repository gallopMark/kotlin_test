package com.haoyuinfo.app.fragment.menu

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.haoyuinfo.app.R
import com.haoyuinfo.app.adapterhelper.BaseArrayRecyclerAdapter
import com.haoyuinfo.library.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_userinfo.*

/**
 * 创建日期：2018/3/22.
 * 描述:个人信息
 * 作者:xiaoma
 */
class UserInfoFragment : BaseFragment() {
    override fun setLayoutResID(): Int {
        return R.layout.fragment_userinfo
    }

    override fun setUp() {
        val mDatas = ArrayList<String>().apply {
            for (i in 0 until 20) {
                this.add("test ${i + 1}")
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(context).apply { orientation = LinearLayoutManager.VERTICAL }
        val adapter = MyAdapter(mDatas)
        recyclerView.adapter = adapter
    }

    private inner class MyAdapter(mDatas: MutableList<String>) : BaseArrayRecyclerAdapter<String>(mDatas) {
        override fun bindView(viewType: Int): Int {
            return R.layout.mytrain_item
        }

        override fun onBindHoder(holder: RecyclerHolder, t: String, position: Int) {
            val mTrainTv = holder.obtainView<TextView>(R.id.mTrainTv)
            val mSelectIv = holder.obtainView<ImageView>(R.id.mSelectIv)
            mTrainTv.text = t
            mSelectIv.visibility = View.GONE
        }

    }
}