package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName

/**
 * 创建日期：2018/3/19.
 * 描述: 消息实体类
 * id	消息id	String	Y
 * title	标题	String	Y
 * content	内容	String	Y
 * type	类型	String	Y	系统消息：“system_message”
 * 小纸条：“user_message”
 * sender	发送人	MUser	Y	MUser详见公共对象
 * receiver	接收人	MUser	Y	MUser详见公共对象
 * 作者:xiaoma
 */
class Message {
    companion object {
        const val TYPE_SYSTEM = "system_message"
        const val TYPE_USER = "user_message"
        const val TYPE_DAILY_WARN = "study_daily_warn"
        const val TEXT_SYSTEM = "系统消息"
        const val TEXT_DAILY_WARN = "每日提醒"
    }

    @SerializedName("id")
    var id: String? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("content")
    var content: String? = null
    @SerializedName("type")
    var type: String? = null
    @SerializedName("createTime")
    var createTime: Long = 0
    @SerializedName("sender")
    var sender: MobileUser? = null
    @SerializedName("receiver")
    var receiver: MobileUser? = null
}