package com.haoyuinfo.app.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.haoyuinfo.app.R
import com.haoyuinfo.app.adapterhelper.BaseArrayRecyclerAdapter
import com.haoyuinfo.app.entity.DiscussEntity
import com.haoyuinfo.library.utils.GlideUtils
import com.haoyuinfo.library.utils.TimeUtils

/**
 * 创建日期：2018/3/22.
 * 描述:课程学习讨论适配器
 * 作者:xiaoma
 */
class CourseDiscussAdapter(private val context: Context, mDatas: MutableList<DiscussEntity>) : BaseArrayRecyclerAdapter<DiscussEntity>(mDatas) {
    override fun bindView(viewType: Int): Int {
        return R.layout.course_discuss_item
    }

    override fun onBindHoder(holder: RecyclerHolder, t: DiscussEntity, position: Int) {
        val mTitleTv = holder.obtainView<TextView>(R.id.mTitleTv)
        val mContentTv = holder.obtainView<TextView>(R.id.mContentTv)
        val mUserIv = holder.obtainView<ImageView>(R.id.mUserIv)
        val mUserNameTv = holder.obtainView<TextView>(R.id.mUserNameTv)
        val mCreateTimeTv = holder.obtainView<TextView>(R.id.mCreateTimeTv)
        val mSupportTv = holder.obtainView<TextView>(R.id.mSupportTv)
        val mDiscussTv = holder.obtainView<TextView>(R.id.mDiscussTv)
        mTitleTv.text = t.title
        mContentTv.text = t.content
        GlideUtils.loadCircleImage(context, t.creator?.avatar, R.drawable.ic_user_default, R.drawable.ic_user_default, mUserIv)
        mUserNameTv.text = t.creator?.realName
        mCreateTimeTv.text = TimeUtils.converTime(t.createTime)
        var supportNum = 0
        var replyNum = 0
        t.relation()?.let {
            supportNum = it.supportNum
            replyNum = it.replyNum
        }
        mSupportTv.text = "$supportNum"
        mDiscussTv.text = "$replyNum"
    }
}