package com.haoyuinfo.app.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.TextView
import com.haoyuinfo.app.R
import com.haoyuinfo.app.adapterhelper.BaseArrayRecyclerAdapter
import com.haoyuinfo.app.entity.Message
import com.haoyuinfo.library.utils.TimeUtils

/**
 * 创建日期：2018/3/19.
 * 描述:消息适配器
 * 作者:xiaoma
 */
class MessageAdapter(private val context: Context, mDatas: MutableList<Message>) :
        BaseArrayRecyclerAdapter<Message>(mDatas) {
    override fun bindView(viewType: Int): Int {
        return R.layout.message_item
    }

    override fun onBindHoder(holder: RecyclerHolder, t: Message, position: Int) {
        val mTypeTv = holder.obtainView<TextView>(R.id.mTypeTv)
        val mTimeTv = holder.obtainView<TextView>(R.id.mTimeTv)
        val mContentTv = holder.obtainView<TextView>(R.id.mContentTv)
        val mReplyTv = holder.obtainView<TextView>(R.id.mReplyTv)
        val divider = holder.obtainView<View>(R.id.divider)
        when {
            t.type == Message.TYPE_USER -> {
                mTypeTv.text = t.sender?.realName
                mTypeTv.setTextColor(ContextCompat.getColor(context, R.color.darkorange))
                mTypeTv.setBackgroundResource(R.drawable.shape_message_user)
                mReplyTv.visibility = View.VISIBLE
                mReplyTv.text = "回复"
            }
            t.type == Message.TYPE_DAILY_WARN -> {
                mTypeTv.text = Message.TEXT_DAILY_WARN
                mTypeTv.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                mTypeTv.setBackgroundResource(R.drawable.shape_message_sys)
                mReplyTv.visibility = View.GONE
            }
            else -> {
                mTypeTv.text = Message.TEXT_SYSTEM
                mTypeTv.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                mTypeTv.setBackgroundResource(R.drawable.shape_message_sys)
                mReplyTv.visibility = View.GONE
            }
        }
        mTimeTv.text = TimeUtils.getSlashDate(t.createTime)
        mContentTv.text = t.content
        divider.visibility = if (itemCount == mDatas.size - 1) View.GONE else View.VISIBLE
    }
}