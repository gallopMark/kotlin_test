package com.haoyuinfo.app.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.haoyuinfo.app.R
import com.haoyuinfo.app.adapterhelper.BaseArrayRecyclerAdapter
import com.haoyuinfo.app.entity.MobileUser
import com.haoyuinfo.library.utils.GlideUtils

class PeerAdapter(private val context: Context, mDatas: MutableList<MobileUser>) : BaseArrayRecyclerAdapter<MobileUser>(mDatas) {
    override fun bindView(viewType: Int): Int {
        return R.layout.peer_item
    }

    override fun onBindHoder(holder: RecyclerHolder, t: MobileUser, position: Int) {
        val ivIco = holder.obtainView<ImageView>(R.id.ivIco)
        val tvName = holder.obtainView<TextView>(R.id.tvName)
        GlideUtils.loadCircleImage(context, t.avatar, R.drawable.ic_user_default, R.drawable.ic_user_default, ivIco)
        tvName.text = t.realName
    }
}