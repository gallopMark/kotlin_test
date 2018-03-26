package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName
import com.haoyu.app.entity.Paginator
import com.haoyuinfo.app.base.BaseResult
import java.util.*

/**
 * 创建日期：2018/3/19.
 * 描述:消息列表
 * 作者:xiaoma
 */
class MessageResult : BaseResult<MessageResult.MData>() {
    inner class MData {
        @SerializedName("mMessages")
        var mMessages: List<Message>? = null
            get() = if (field == null) ArrayList() else field
        @SerializedName("paginator")
        var paginator: Paginator? = null
    }
}