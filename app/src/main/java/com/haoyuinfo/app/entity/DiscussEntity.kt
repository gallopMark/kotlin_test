package com.haoyuinfo.app.entity

import com.google.gson.annotations.SerializedName

class DiscussEntity {
    @SerializedName("id")
    var id: String? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("content")
    var content: String? = null
    @SerializedName("creator")
    var creator: MobileUser? = null
    @SerializedName("createTime")
    var createTime: Long = 0
    @SerializedName("mDiscussionRelations")
    private var relations: List<DiscussRelation>? = null
    @SerializedName("mainPostNum")
    var mainPostNum: Int = 0
    @SerializedName("subPostNum")
    var subPostNum: Int = 0
    @SerializedName("mFileInfos")
    var fileInfos: List<MFileInfo>? = null
        get() = if (field == null) ArrayList() else field

    fun relation(): DiscussRelation? {
        relations?.let { if (it.isNotEmpty()) return it[0] }
        return null
    }

    inner class DiscussRelation {
        @SerializedName("id")
        var id: String? = null
        @SerializedName("replyNum")
        var replyNum: Int = 0
        @SerializedName("supportNum")
        var supportNum: Int = 0
        @SerializedName("participateNum")
        var participateNum: Int = 0
        @SerializedName("followNum")
        var followNum: Int = 0
        @SerializedName("browseNum")
        var browseNum: Int = 0
    }
}